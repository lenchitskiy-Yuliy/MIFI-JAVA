package infrastructure.storage;

import application.port.Inbox;
import domain.link.LinkStatus;
import domain.link.Notification;
import domain.link.Url;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FileInbox implements Inbox {
  private final CsvFile file;
  private final List<Notification> notifications = new ArrayList<>();

  public FileInbox(Path file) {
    this.file = new CsvFile(file);

    List<List<String>> rows = this.file.read();

    for (int i = 0; i < rows.size(); i++) {
      notifications.add(parse(rows.get(i), i + 1));
    }
  }

  @Override
  public void send(Notification notification) {
    notifications.add(notification);
    flush();
  }

  @Override
  public List<Notification> take(UUID owner) {
    List<Notification> taken =
        notifications.stream().filter(item -> item.owner().equals(owner)).toList();

    if (!taken.isEmpty()) {
      notifications.removeAll(taken);
      flush();
    }

    return taken;
  }

  @Override
  public boolean has(UUID owner) {
    return notifications.stream().anyMatch(item -> item.owner().equals(owner));
  }

  private void flush() {
    file.write(
        notifications.stream()
            .map(item -> Csv.line(item.owner(), item.code(), item.reason(), item.url()))
            .toList());
  }

  private Notification parse(List<String> row, int line) {
    try {
      return new Notification(
          UUID.fromString(row.get(0)),
          row.get(1),
          Url.parse(row.get(3)),
          LinkStatus.valueOf(row.get(2)));
    } catch (RuntimeException exception) {
      throw file.broken(line, exception);
    }
  }
}
