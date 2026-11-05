package app;

import application.LinkSettings;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record AppConfig(
    Path dataDirectory,
    String domain,
    int codeLength,
    LinkSettings links,
    Duration cleanupInterval) {
  private static final String DEFAULTS = "/application.properties";
  private static final Pattern DURATION = Pattern.compile("(\\d{1,6})\\s*([smhd])");

  public static AppConfig load(String[] args) {
    if (args.length > 1) {
      throw new IllegalArgumentException("Использование: java -jar short-link.jar [файл настроек]");
    }

    Properties properties = defaults();

    if (args.length == 1) {
      Path file = Path.of(args[0]);

      try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
        properties.load(reader);
      } catch (IOException exception) {
        throw new IllegalArgumentException("Не удалось прочитать настройки " + file);
      }
    }

    return from(properties);
  }

  static AppConfig from(Properties properties) {
    Settings settings = new Settings(properties);
    int codeLength = settings.number("link.code-length");

    if (codeLength < 4 || codeLength > 12) {
      throw new IllegalArgumentException("link.code-length должен быть от 4 до 12");
    }

    return new AppConfig(
        Path.of(settings.text("storage.directory")),
        settings.text("link.domain"),
        codeLength,
        new LinkSettings(
            settings.duration("link.ttl"),
            settings.number("link.default-limit"),
            settings.number("link.max-limit")),
        settings.duration("link.cleanup-interval"));
  }

  private static Properties defaults() {
    Properties properties = new Properties();

    try (InputStream stream = AppConfig.class.getResourceAsStream(DEFAULTS)) {
      properties.load(stream);
    } catch (IOException | NullPointerException exception) {
      throw new IllegalStateException("Нет файла настроек по умолчанию " + DEFAULTS);
    }

    return properties;
  }

  private record Settings(Properties properties) {
    String text(String key) {
      String value = properties.getProperty(key, "").strip();

      if (value.isEmpty()) {
        throw new IllegalArgumentException("Не задан параметр " + key);
      }

      return value;
    }

    int number(String key) {
      try {
        return Integer.parseInt(text(key));
      } catch (NumberFormatException exception) {
        throw new IllegalArgumentException(key + " должен быть целым числом");
      }
    }

    Duration duration(String key) {
      Matcher matcher = DURATION.matcher(text(key));

      if (!matcher.matches()) {
        throw new IllegalArgumentException(key + " задаётся как 30s, 15m, 24h или 7d");
      }

      int amount = Integer.parseInt(matcher.group(1));

      if (amount == 0) {
        throw new IllegalArgumentException(key + " должен быть больше нуля");
      }

      return switch (matcher.group(2)) {
        case "s" -> Duration.ofSeconds(amount);
        case "m" -> Duration.ofMinutes(amount);
        case "h" -> Duration.ofHours(amount);
        default -> Duration.ofDays(amount);
      };
    }
  }
}
