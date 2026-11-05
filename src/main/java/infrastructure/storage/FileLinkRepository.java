package infrastructure.storage;

import application.port.LinkRepository;
import domain.link.ShortLink;
import domain.link.Url;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class FileLinkRepository implements LinkRepository {
  private final CsvFile file;
  private final Map<String, ShortLink> links = new LinkedHashMap<>();

  public FileLinkRepository(Path file) {
    this.file = new CsvFile(file);

    List<List<String>> rows = this.file.read();

    for (int i = 0; i < rows.size(); i++) {
      ShortLink link = parse(rows.get(i), i + 1);
      links.put(link.code(), link);
    }
  }

  @Override
  public Optional<ShortLink> find(String code) {
    return Optional.ofNullable(links.get(code));
  }

  @Override
  public List<ShortLink> findByOwner(UUID owner) {
    return links.values().stream().filter(link -> link.owner().equals(owner)).toList();
  }

  @Override
  public List<ShortLink> findAll() {
    return new ArrayList<>(links.values());
  }

  @Override
  public void save(ShortLink link) {
    links.put(link.code(), link);
    flush();
  }

  @Override
  public void delete(String code) {
    links.remove(code);
    flush();
  }

  private void flush() {
    file.write(
        links.values().stream()
            .map(
                link ->
                    Csv.line(
                        link.code(),
                        link.owner(),
                        link.url(),
                        link.createdAt(),
                        link.expiresAt(),
                        link.limit(),
                        link.clicks()))
            .toList());
  }

  private ShortLink parse(List<String> row, int line) {
    try {
      return new ShortLink(
          row.get(0),
          UUID.fromString(row.get(1)),
          Url.parse(row.get(2)),
          Instant.parse(row.get(3)),
          Instant.parse(row.get(4)),
          Integer.parseInt(row.get(5)),
          Integer.parseInt(row.get(6)));
    } catch (RuntimeException exception) {
      throw file.broken(line, exception);
    }
  }
}
