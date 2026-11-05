package infrastructure.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

final class CsvFile {
  private final Path file;

  CsvFile(Path file) {
    this.file = file;
  }

  List<List<String>> read() {
    if (!Files.exists(file)) {
      return List.of();
    }

    try {
      return Files.readAllLines(file, StandardCharsets.UTF_8).stream()
          .filter(line -> !line.isBlank())
          .map(Csv::parse)
          .toList();
    } catch (IOException exception) {
      throw new IllegalStateException("Не удалось прочитать " + file, exception);
    }
  }

  void write(List<String> lines) {
    try {
      Path temp = file.resolveSibling(file.getFileName() + ".tmp");

      Files.createDirectories(file.toAbsolutePath().getParent());
      Files.write(temp, lines, StandardCharsets.UTF_8);
      Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    } catch (IOException exception) {
      throw new IllegalStateException("Не удалось сохранить " + file, exception);
    }
  }

  IllegalStateException broken(int line, Exception cause) {
    return new IllegalStateException("Файл " + file + " повреждён, строка " + line, cause);
  }
}
