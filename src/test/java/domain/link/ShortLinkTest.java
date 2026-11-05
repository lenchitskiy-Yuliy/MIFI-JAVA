package domain.link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ShortLinkTest {
  private static final Instant NOW = Instant.parse("2026-11-05T10:00:00Z");
  private final UUID owner = UUID.randomUUID();
  private final ShortLink link =
      ShortLink.create(
          "abc123", owner, Url.parse("https://example.com"), NOW, Duration.ofHours(1), 2);

  @Test
  void blocksAfterLimit() {
    link.visit(NOW);
    link.visit(NOW);

    assertEquals(LinkStatus.EXHAUSTED, link.status(NOW));
    assertThrows(IllegalStateException.class, () -> link.visit(NOW));
    assertEquals(2, link.clicks());
  }

  @Test
  void expiresAfterTtl() {
    assertEquals(LinkStatus.ACTIVE, link.status(NOW.plus(Duration.ofMinutes(59))));
    assertEquals(LinkStatus.EXPIRED, link.status(NOW.plus(Duration.ofHours(1))));
    assertThrows(IllegalStateException.class, () -> link.visit(NOW.plus(Duration.ofHours(2))));
  }

  @Test
  void limitCanBeRaisedButNotBelowClicks() {
    link.visit(NOW);
    link.visit(NOW);
    link.changeLimit(3);

    assertEquals(LinkStatus.ACTIVE, link.status(NOW));
    assertThrows(IllegalArgumentException.class, () -> link.changeLimit(2));
  }

  @Test
  void onlyOwnerPasses() {
    link.checkOwner(owner);

    assertThrows(IllegalStateException.class, () -> link.checkOwner(UUID.randomUUID()));
  }

  @Test
  void addsSchemeToUrl() {
    assertEquals("https://ya.ru/search", Url.parse("ya.ru/search").toString());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"", "ftp://example.com", "https://", "http://exa mple.com", "javascript:alert(1)"})
  void rejectsBadUrl(String value) {
    assertThrows(IllegalArgumentException.class, () -> Url.parse(value));
  }
}
