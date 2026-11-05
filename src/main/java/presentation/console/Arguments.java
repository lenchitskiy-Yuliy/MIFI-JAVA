package presentation.console;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Function;

public final class Arguments {
  private final List<String> values;

  public Arguments(List<String> values) {
    this.values = List.copyOf(values);
  }

  public String get(int index) {
    return values.get(index);
  }

  public <T> T get(int index, Function<String, T> parser) {
    return parser.apply(get(index));
  }

  public boolean has(int index) {
    return index < values.size();
  }

  public List<String> from(int index) {
    return values.subList(Math.min(index, values.size()), values.size());
  }

  public LocalDate date(int index, LocalDate fallback) {
    return has(index) ? get(index, Arguments::parseDate) : fallback;
  }

  public static LocalDate parseDate(String value) {
    try {
      return LocalDate.parse(value);
    } catch (DateTimeParseException exception) {
      throw new IllegalArgumentException("Дата должна быть в формате ГГГГ-ММ-ДД: " + value);
    }
  }
}
