package domain.link;

import java.util.UUID;

public record Notification(UUID owner, String code, Url url, LinkStatus reason) {
  public static Notification about(ShortLink link, LinkStatus reason) {
    return new Notification(link.owner(), link.code(), link.url(), reason);
  }
}
