package domain.link;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public final class ShortLink {
  private final String code;
  private final UUID owner;
  private final Instant createdAt;
  private final Instant expiresAt;
  private Url url;
  private int limit;
  private int clicks;

  public ShortLink(
      String code,
      UUID owner,
      Url url,
      Instant createdAt,
      Instant expiresAt,
      int limit,
      int clicks) {
    if (limit < 1 || clicks < 0 || clicks > limit) {
      throw new IllegalArgumentException("Некорректные переходы ссылки " + code);
    }

    this.code = code;
    this.owner = owner;
    this.url = url;
    this.createdAt = createdAt;
    this.expiresAt = expiresAt;
    this.limit = limit;
    this.clicks = clicks;
  }

  public static ShortLink create(
      String code, UUID owner, Url url, Instant now, Duration ttl, int limit) {
    return new ShortLink(code, owner, url, now, now.plus(ttl), limit, 0);
  }

  public LinkStatus status(Instant now) {
    if (isExpired(now)) {
      return LinkStatus.EXPIRED;
    }

    return isExhausted() ? LinkStatus.EXHAUSTED : LinkStatus.ACTIVE;
  }

  public boolean isExpired(Instant now) {
    return !now.isBefore(expiresAt);
  }

  public boolean isExhausted() {
    return clicks >= limit;
  }

  public Url visit(Instant now) {
    LinkStatus status = status(now);

    if (status != LinkStatus.ACTIVE) {
      throw new IllegalStateException("Ссылка недоступна: " + status.title());
    }

    clicks++;
    return url;
  }

  public void changeLimit(int limit) {
    if (limit <= clicks) {
      throw new IllegalArgumentException(
          "Лимит должен быть больше уже сделанных переходов: " + clicks);
    }

    this.limit = limit;
  }

  public void changeUrl(Url url) {
    this.url = url;
  }

  public void checkOwner(UUID user) {
    if (!owner.equals(user)) {
      throw new IllegalStateException("Это чужая ссылка, менять её может только владелец");
    }
  }

  public String code() {
    return code;
  }

  public UUID owner() {
    return owner;
  }

  public Url url() {
    return url;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant expiresAt() {
    return expiresAt;
  }

  public int limit() {
    return limit;
  }

  public int clicks() {
    return clicks;
  }
}
