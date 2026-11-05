package application;

import application.port.CodeGenerator;
import application.port.Inbox;
import application.port.LinkRepository;
import domain.link.LinkStatus;
import domain.link.Notification;
import domain.link.ShortLink;
import domain.link.Url;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class LinkService {
  private static final int ATTEMPTS = 10;

  private final LinkRepository links;
  private final CodeGenerator codes;
  private final Inbox inbox;
  private final LinkSettings settings;
  private final Clock clock;

  public LinkService(
      LinkRepository links, CodeGenerator codes, Inbox inbox, LinkSettings settings, Clock clock) {
    this.links = links;
    this.codes = codes;
    this.inbox = inbox;
    this.settings = settings;
    this.clock = clock;
  }

  public ShortLink create(UUID owner, String url) {
    return create(owner, url, settings.defaultLimit());
  }

  public synchronized ShortLink create(UUID owner, String url, int limit) {
    ShortLink link =
        ShortLink.create(freeCode(), owner, Url.parse(url), now(), settings.ttl(), check(limit));

    links.save(link);
    return link;
  }

  public synchronized ShortLink open(String code) {
    ShortLink link = find(code);
    link.visit(now());
    links.save(link);

    if (link.isExhausted()) {
      inbox.send(Notification.about(link, LinkStatus.EXHAUSTED));
    }

    return link;
  }

  public synchronized List<ShortLink> of(UUID owner) {
    removeExpired();
    return links.findByOwner(owner);
  }

  public synchronized ShortLink changeLimit(UUID owner, String code, int limit) {
    ShortLink link = owned(owner, code);
    link.changeLimit(check(limit));
    links.save(link);
    return link;
  }

  public synchronized ShortLink changeUrl(UUID owner, String code, String url) {
    ShortLink link = owned(owner, code);
    link.changeUrl(Url.parse(url));
    links.save(link);
    return link;
  }

  public synchronized void delete(UUID owner, String code) {
    owned(owner, code);
    links.delete(code);
  }

  public synchronized List<ShortLink> removeExpired() {
    List<ShortLink> expired =
        links.findAll().stream().filter(link -> link.isExpired(now())).toList();

    expired.forEach(this::expire);
    return expired;
  }

  public synchronized List<Notification> notifications(UUID owner) {
    removeExpired();
    return inbox.take(owner);
  }

  public synchronized boolean knows(UUID user) {
    return !links.findByOwner(user).isEmpty() || inbox.has(user);
  }

  private ShortLink find(String code) {
    ShortLink link =
        links
            .find(code)
            .orElseThrow(() -> new IllegalArgumentException("Ссылка не найдена: " + code));

    if (link.isExpired(now())) {
      expire(link);
      throw new IllegalStateException("Ссылка недоступна: срок жизни истёк, она удалена");
    }

    return link;
  }

  private ShortLink owned(UUID owner, String code) {
    ShortLink link = find(code);
    link.checkOwner(owner);
    return link;
  }

  private void expire(ShortLink link) {
    links.delete(link.code());
    inbox.send(Notification.about(link, LinkStatus.EXPIRED));
  }

  private String freeCode() {
    for (int i = 0; i < ATTEMPTS; i++) {
      String code = codes.next();

      if (links.find(code).isEmpty()) {
        return code;
      }
    }

    throw new IllegalStateException("Не удалось подобрать свободный код, попробуйте ещё раз");
  }

  private int check(int limit) {
    if (limit < 1 || limit > settings.maxLimit()) {
      throw new IllegalArgumentException(
          "Лимит переходов должен быть от 1 до " + settings.maxLimit());
    }

    return limit;
  }

  private Instant now() {
    return clock.instant();
  }
}
