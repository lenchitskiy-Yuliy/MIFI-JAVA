package presentation.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public final class Console {
  private final Terminal terminal;
  private final Supplier<String> prompt;
  private final Map<String, Command> commands = new LinkedHashMap<>();
  private final List<String> hints = new ArrayList<>();
  private boolean running;

  public Console(Terminal terminal, Supplier<String> prompt, List<Command> commands) {
    this.terminal = terminal;
    this.prompt = prompt;

    add(new Help());
    commands.forEach(this::add);
    add(new Exit());
  }

  public void run(BufferedReader input) throws IOException {
    running = true;

    while (running) {
      terminal.print(Text.of(prompt.get() + "> ", Color.CYAN));
      String line = input.readLine();

      if (line == null) {
        terminal.println();
        return;
      }

      handle(line);
    }
  }

  public void handle(String line) {
    try {
      List<String> tokens = split(line);

      if (tokens.isEmpty()) {
        return;
      }

      Command command = commands.get(tokens.get(0).toLowerCase(Locale.ROOT));

      if (command != null) {
        command.run(tokens.subList(1, tokens.size()));
        return;
      }

      commands.values().stream()
          .filter(item -> item.catches(tokens.get(0)))
          .findFirst()
          .orElseThrow(
              () ->
                  new IllegalArgumentException(
                      "Неизвестная команда: " + tokens.get(0) + ". Список команд: help"))
          .run(tokens);
    } catch (IllegalArgumentException | IllegalStateException exception) {
      terminal.error(exception.getMessage());
    } catch (RuntimeException exception) {
      terminal.error("Не удалось выполнить команду: " + exception);
    }
  }

  public Console hint(String hint) {
    hints.add(hint);
    return this;
  }

  private void add(Command command) {
    commands.put(command.name(), command);
  }

  static List<String> split(String line) {
    List<String> tokens = new ArrayList<>();
    StringBuilder token = new StringBuilder();
    boolean quoted = false;
    boolean started = false;

    for (char c : line.toCharArray()) {
      if (c == '"') {
        quoted = !quoted;
        started = true;
      } else if (Character.isWhitespace(c) && !quoted) {
        if (started) {
          tokens.add(token.toString());
          token.setLength(0);
          started = false;
        }
      } else {
        token.append(c);
        started = true;
      }
    }

    if (quoted) {
      throw new IllegalArgumentException("Не закрыта кавычка");
    }

    if (started) {
      tokens.add(token.toString());
    }

    return tokens;
  }

  private final class Help extends Command {
    Help() {
      super("help", "", "Список команд");
    }

    @Override
    protected void execute(Arguments args) {
      Table table = new Table("Команда", "Описание");
      commands.values().forEach(command -> table.row(command.usage(), command.description()));
      table.print(terminal);
      hints.forEach(terminal::hint);
    }
  }

  private final class Exit extends Command {
    Exit() {
      super("exit", "", "Выйти");
    }

    @Override
    protected void execute(Arguments args) {
      running = false;
    }
  }
}
