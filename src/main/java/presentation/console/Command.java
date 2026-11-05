package presentation.console;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class Command {
  private final String name;
  private final String params;
  private final String description;

  protected Command(String name, String params, String description) {
    this.name = name;
    this.params = params;
    this.description = description;
  }

  public String name() {
    return name;
  }

  public String usage() {
    return params.isEmpty() ? name : name + " " + params;
  }

  public String description() {
    return description;
  }

  public boolean catches(String input) {
    return false;
  }

  public final void run(List<String> values) {
    if (values.size() < required() || values.size() > maximum()) {
      throw new IllegalArgumentException("Использование: " + usage());
    }

    execute(new Arguments(values));
  }

  protected abstract void execute(Arguments args);

  private long required() {
    return params().filter(param -> param.startsWith("<")).count();
  }

  private long maximum() {
    return params().anyMatch(param -> param.endsWith("...]")) ? Long.MAX_VALUE : params().count();
  }

  private Stream<String> params() {
    return Arrays.stream(params.split(" ")).filter(param -> !param.isEmpty());
  }
}
