package domain.link;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public record Url(URI value) {
  private static final int MAX_LENGTH = 2048;

  public static Url parse(String text) {
    String value = text.strip();

    if (value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException("Адрес длиннее " + MAX_LENGTH + " символов");
    }

    if (!value.contains("://")) {
      value = "https://" + value;
    }

    try {
      URI uri = new URI(value);
      String scheme = uri.getScheme().toLowerCase(Locale.ROOT);

      if ((!scheme.equals("http") && !scheme.equals("https")) || uri.getHost() == null) {
        throw invalid(text);
      }

      return new Url(uri);
    } catch (URISyntaxException exception) {
      throw invalid(text);
    }
  }

  private static IllegalArgumentException invalid(String text) {
    return new IllegalArgumentException(
        "Некорректный адрес: " + text + ". Пример: https://example.com/page");
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
