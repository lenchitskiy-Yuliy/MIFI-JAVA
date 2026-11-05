package presentation.command;

import domain.link.LinkStatus;
import domain.link.Notification;
import domain.link.ShortLink;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public final class LinkFormat {
  private static final DateTimeFormatter TIME =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());
  private static final Pattern CODE = Pattern.compile("[0-9A-Za-z]+");
  private static final Pattern SCHEME = Pattern.compile("^https?://", Pattern.CASE_INSENSITIVE);

  private final String domain;

  public LinkFormat(String domain) {
    this.domain = domain;
  }

  public String of(ShortLink link) {
    return of(link.code());
  }

  public String of(String code) {
    return domain + "/" + code;
  }

  public boolean isShort(String input) {
    String prefix = domain + "/";
    return withoutScheme(input).regionMatches(true, 0, prefix, 0, prefix.length());
  }

  public String requireLong(String url) {
    if (isShort(url)) {
      throw new IllegalArgumentException("Это уже короткая ссылка: " + url);
    }

    return url;
  }

  public String code(String input) {
    String value = withoutScheme(input);
    String code = isShort(value) ? value.substring(domain.length() + 1) : value;

    if (!CODE.matcher(code).matches()) {
      throw new IllegalArgumentException("Некорректная короткая ссылка: " + input);
    }

    return code;
  }

  public String time(Instant instant) {
    return TIME.format(instant);
  }

  public String duration(Duration duration) {
    if (duration.toDays() > 0 && duration.toHours() % 24 == 0) {
      return duration.toDays() + " д";
    }

    if (duration.toHours() > 0 && duration.toMinutes() % 60 == 0) {
      return duration.toHours() + " ч";
    }

    return duration.toMinutes() > 0 ? duration.toMinutes() + " мин" : duration.toSeconds() + " с";
  }

  public String notice(Notification notification) {
    String text =
        "Ссылка "
            + of(notification.code())
            + " на "
            + notification.url()
            + " больше не работает: "
            + notification.reason().title();

    return notification.reason() == LinkStatus.EXPIRED ? text + ", она удалена" : text;
  }

  private static String withoutScheme(String input) {
    return SCHEME.matcher(input.strip()).replaceFirst("");
  }
}
