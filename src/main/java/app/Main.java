package app;

import infrastructure.browser.DesktopBrowser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Objects;
import presentation.console.Terminal;

public final class Main {
  private Main() {}

  public static void main(String[] args) {
    try {
      AppConfig config = AppConfig.load(args);
      BufferedReader input =
          new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

      new ShortenerApp(config, Terminal.system(), new DesktopBrowser(), Clock.systemUTC())
          .run(input);
      System.exit(0);
    } catch (Exception exception) {
      System.err.println(
          "Ошибка: " + Objects.requireNonNullElse(exception.getMessage(), exception));
      System.exit(1);
    }
  }
}
