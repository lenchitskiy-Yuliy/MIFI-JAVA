package presentation.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LinkFormatTest {
  private final LinkFormat format = new LinkFormat("mifi.test");

  @Test
  void readsCodeFromAnyForm() {
    assertEquals("3DZHeG", format.code("mifi.test/3DZHeG"));
    assertEquals("3DZHeG", format.code("https://mifi.test/3DZHeG"));
    assertEquals("3DZHeG", format.code("HTTPS://MIFI.TEST/3DZHeG"));
    assertEquals("3DZHeG", format.code("3DZHeG"));
    assertThrows(IllegalArgumentException.class, () -> format.code("mifi.test/../etc"));
  }

  @Test
  void recognizesShortLink() {
    assertTrue(format.isShort("mifi.test/abc"));
    assertFalse(format.isShort("https://example.com/abc"));
    assertThrows(IllegalArgumentException.class, () -> format.requireLong("https://mifi.test/abc"));
  }
}
