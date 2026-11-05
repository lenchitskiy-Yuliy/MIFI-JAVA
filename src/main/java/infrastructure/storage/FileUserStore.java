package infrastructure.storage;

import application.port.UserStore;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public final class FileUserStore implements UserStore {
  private final Path file;

  public FileUserStore(Path file) {
    this.file = file;
  }

  @Override
  public Optional<UUID> load() {
    try {
      return Files.exists(file)
          ? Optional.of(UUID.fromString(Files.readString(file, StandardCharsets.UTF_8).strip()))
          : Optional.empty();
    } catch (IOException | IllegalArgumentException exception) {
      return Optional.empty();
    }
  }

  @Override
  public void save(UUID user) {
    try {
      Files.createDirectories(file.toAbsolutePath().getParent());
      Files.writeString(file, user.toString(), StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new IllegalStateException("Не удалось сохранить UUID в " + file, exception);
    }
  }

  @Override
  public void clear() {
    try {
      Files.deleteIfExists(file);
    } catch (IOException exception) {
      throw new IllegalStateException("Не удалось удалить " + file, exception);
    }
  }
}
