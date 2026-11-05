package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppConfigTest {
  @TempDir Path dir;

  @Test
  void readsDefaultsAndOverrides() throws Exception {
    Path file = dir.resolve("app.properties");
    Files.writeString(file, "link.ttl=2h\nlink.default-limit=3\n");

    AppConfig config = AppConfig.load(new String[] {file.toString()});

    assertEquals(Duration.ofHours(2), config.links().ttl());
    assertEquals(3, config.links().defaultLimit());
    assertEquals(1000, config.links().maxLimit());
    assertEquals("mifi.test", config.domain());
  }

  @Test
  void rejectsBadValues() throws Exception {
    Path file = dir.resolve("app.properties");
    Files.writeString(file, "link.ttl=сутки\n");

    assertThrows(
        IllegalArgumentException.class, () -> AppConfig.load(new String[] {file.toString()}));
    assertThrows(
        IllegalArgumentException.class, () -> AppConfig.load(new String[] {"missing.properties"}));

    Files.writeString(file, "link.cleanup-interval=0s\n");
    assertThrows(
        IllegalArgumentException.class, () -> AppConfig.load(new String[] {file.toString()}));
  }
}
