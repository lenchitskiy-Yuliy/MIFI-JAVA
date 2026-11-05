package app;

import java.nio.file.Path;

public record AppSettings(Path dataDirectory) {
  private static final String DEFAULT_DIRECTORY = "data";

  public static AppSettings from(String[] args) {
    if (args.length > 1) {
      throw new IllegalArgumentException("Использование: java -jar finance.jar [каталог данных]");
    }

    return new AppSettings(Path.of(args.length == 1 ? args[0] : DEFAULT_DIRECTORY));
  }
}
