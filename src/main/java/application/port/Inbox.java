package application.port;

import domain.link.Notification;
import java.util.List;
import java.util.UUID;

public interface Inbox {
  void send(Notification notification);

  List<Notification> take(UUID owner);

  boolean has(UUID owner);
}
