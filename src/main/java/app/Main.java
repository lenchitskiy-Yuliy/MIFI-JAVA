package app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import presentation.console.Terminal;

public final class Main {
  private Main() {}

  public static void main(String[] args) {
    try {
      AppSettings settings = AppSettings.from(args);
      BufferedReader input =
          new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

      FinanceApp app = new FinanceApp(settings, Terminal.system());

      Runtime.getRuntime().addShutdownHook(new Thread(app::shutdown));
      app.run(input);
    } catch (Exception exception) {
      System.err.println(
          "Ошибка: " + Objects.requireNonNullElse(exception.getMessage(), exception));
      System.exit(1);
    }
  }
}
