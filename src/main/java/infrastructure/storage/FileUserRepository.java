package infrastructure.storage;

import application.port.UserRepository;
import domain.user.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class FileUserRepository implements UserRepository {
  private final Path file;
  private final Map<String, User> users = new LinkedHashMap<>();

  public FileUserRepository(Path file) {
    this.file = file;

    if (Files.exists(file)) {
      read().forEach(user -> users.put(key(user.login()), user));
    }
  }

  @Override
  public Optional<User> find(String login) {
    return Optional.ofNullable(users.get(key(login)));
  }

  @Override
  public void save(User user) {
    users.put(key(user.login()), user);

    List<String> lines =
        users.values().stream().map(item -> Csv.line(item.login(), item.passwordHash())).toList();

    Csv.write(file, lines);
  }

  private List<User> read() {
    try {
      return Files.readAllLines(file, StandardCharsets.UTF_8).stream()
          .filter(line -> !line.isBlank())
          .map(Csv::parse)
          .map(row -> new User(row.get(0), row.get(1)))
          .toList();
    } catch (IOException exception) {
      throw new IllegalStateException("Не удалось прочитать " + file, exception);
    }
  }

  private static String key(String login) {
    return login.toLowerCase(Locale.ROOT);
  }
}
