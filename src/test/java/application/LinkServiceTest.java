package application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.port.CodeGenerator;
import domain.link.LinkStatus;
import domain.link.Notification;
import domain.link.ShortLink;
import infrastructure.code.RandomCodeGenerator;
import infrastructure.storage.FileInbox;
import infrastructure.storage.FileLinkRepository;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LinkServiceTest {
  private static final String URL = "https://www.baeldung.com/java-9-http-client";

  @TempDir Path data;

  private final TestClock clock = new TestClock();
  private final UUID alice = UUID.randomUUID();
  private final UUID bob = UUID.randomUUID();

  @Test
  void sameUrlGetsDifferentCodesForDifferentUsers() {
    LinkService service = service(Duration.ofDays(1), new RandomCodeGenerator(6));

    ShortLink first = service.create(alice, URL);
    ShortLink second = service.create(bob, URL);

    assertNotEquals(first.code(), second.code());
    assertEquals(first.url(), second.url());
    assertEquals(List.of(first), service.of(alice));
    assertEquals(List.of(second), service.of(bob));
  }

  @Test
  void skipsTakenCode() {
    Iterator<String> codes = List.of("aaaaaa", "aaaaaa", "bbbbbb").iterator();
    LinkService service = service(Duration.ofDays(1), codes::next);

    service.create(alice, URL);

    assertEquals("bbbbbb", service.create(bob, URL).code());
  }

  @Test
  void blocksAfterLimitAndNotifiesOwner() {
    LinkService service = service(Duration.ofDays(1), new RandomCodeGenerator(6));
    String code = service.create(alice, URL, 2).code();

    service.open(code);
    service.open(code);

    assertThrows(IllegalStateException.class, () -> service.open(code));
    assertEquals(
        List.of(LinkStatus.EXHAUSTED),
        service.notifications(alice).stream().map(Notification::reason).toList());
    assertTrue(service.notifications(alice).isEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {"PT1H", "PT24H"})
  void removesExpiredLinksAndNotifiesOwner(String value) {
    Duration ttl = Duration.parse(value);
    LinkService service = service(ttl, new RandomCodeGenerator(6));
    ShortLink link = service.create(alice, URL);

    clock.advance(ttl.minusSeconds(1));
    assertTrue(service.removeExpired().isEmpty());

    clock.advance(Duration.ofSeconds(1));
    assertEquals(List.of(link), service.removeExpired());
    assertTrue(service.of(alice).isEmpty());
    assertEquals(
        List.of(Notification.about(link, LinkStatus.EXPIRED)), service.notifications(alice));
  }

  @Test
  void openingExpiredLinkDeletesIt() {
    LinkService service = service(Duration.ofHours(1), new RandomCodeGenerator(6));
    String code = service.create(alice, URL).code();

    clock.advance(Duration.ofHours(2));

    assertThrows(IllegalStateException.class, () -> service.open(code));
    assertThrows(IllegalArgumentException.class, () -> service.open(code));
    assertEquals(1, service.notifications(alice).size());
  }

  @Test
  void onlyOwnerCanEditOrDelete() {
    LinkService service = service(Duration.ofDays(1), new RandomCodeGenerator(6));
    String code = service.create(alice, URL).code();

    assertThrows(IllegalStateException.class, () -> service.changeLimit(bob, code, 50));
    assertThrows(IllegalStateException.class, () -> service.changeUrl(bob, code, "ya.ru"));
    assertThrows(IllegalStateException.class, () -> service.delete(bob, code));

    assertEquals(50, service.changeLimit(alice, code, 50).limit());
    assertEquals("https://ya.ru", service.changeUrl(alice, code, "ya.ru").url().toString());
    service.delete(alice, code);
    assertTrue(service.of(alice).isEmpty());
  }

  @Test
  void checksLimitRange() {
    LinkService service = service(Duration.ofDays(1), new RandomCodeGenerator(6));

    assertThrows(IllegalArgumentException.class, () -> service.create(alice, URL, 0));
    assertThrows(IllegalArgumentException.class, () -> service.create(alice, URL, 1001));
  }

  @Test
  void linksSurviveRestart() {
    String code =
        service(Duration.ofDays(1), new RandomCodeGenerator(6)).create(alice, URL, 3).code();
    service(Duration.ofDays(1), new RandomCodeGenerator(6)).open(code);

    ShortLink link = service(Duration.ofDays(1), new RandomCodeGenerator(6)).of(alice).get(0);

    assertEquals(1, link.clicks());
    assertEquals(3, link.limit());
  }

  private LinkService service(Duration ttl, CodeGenerator codes) {
    return new LinkService(
        new FileLinkRepository(data.resolve("links.csv")),
        codes,
        new FileInbox(data.resolve("notifications.csv")),
        new LinkSettings(ttl, 10, 1000),
        clock);
  }
}
