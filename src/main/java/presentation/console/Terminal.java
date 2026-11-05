package presentation.console;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class Terminal {
  private final PrintWriter out;
  private final boolean colors;

  public Terminal(PrintWriter out, boolean colors) {
    this.out = out;
    this.colors = colors;
  }

  public static Terminal system() {
    boolean colors = System.console() != null && System.getenv("NO_COLOR") == null;
    return new Terminal(new PrintWriter(System.out, true, StandardCharsets.UTF_8), colors);
  }

  public String paint(Object value) {
    if (colors && value instanceof Text text && text.color() != null) {
      return text.color().paint(text.value());
    }

    return String.valueOf(value);
  }

  public void print(Object value) {
    out.print(paint(value));
    out.flush();
  }

  public void println(Object value) {
    out.println(paint(value));
  }

  public void println() {
    out.println();
  }

  public void title(String message) {
    println(Text.of(message, Color.BOLD));
  }

  public void success(String message) {
    println(Text.of(message, Color.GREEN));
  }

  public void warning(String message) {
    println(Text.of("Внимание: " + message, Color.YELLOW));
  }

  public void error(String message) {
    println(Text.of("Ошибка: " + message, Color.RED));
  }

  public void hint(String message) {
    println(Text.of(message, Color.GRAY));
  }
}
