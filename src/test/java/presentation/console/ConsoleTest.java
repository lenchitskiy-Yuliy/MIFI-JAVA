package presentation.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConsoleTest {
  private final StringWriter output = new StringWriter();
  private final List<String> received = new ArrayList<>();
  private final Console console =
      new Console(
          new Terminal(new PrintWriter(output, true), false), () -> "test", List.of(new Echo()));

  @Test
  void keepsQuotedTextAsOneArgument() {
    console.handle("echo \"Коммунальные услуги\" 3000");

    assertEquals(List.of("Коммунальные услуги", "3000"), received);
  }

  @Test
  void explainsUsageAndUnknownCommand() {
    console.handle("echo");
    console.handle("unknown");

    assertTrue(output.toString().contains("Использование: echo <текст> [число]"));
    assertTrue(output.toString().contains("Неизвестная команда: unknown"));
    assertTrue(received.isEmpty());
  }

  @Test
  void survivesUnexpectedError() {
    console.handle("ECHO fail");
    console.handle("echo ok");

    assertTrue(output.toString().contains("Не удалось выполнить команду"));
    assertEquals(List.of("ok"), received);
  }

  private final class Echo extends Command {
    Echo() {
      super("echo", "<текст> [число]", "Повторить аргументы");
    }

    @Override
    protected void execute(Arguments args) {
      if (args.get(0).equals("fail")) {
        throw new UnsupportedOperationException("fail");
      }

      received.addAll(args.from(0));
    }
  }
}
