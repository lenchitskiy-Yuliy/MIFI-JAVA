package presentation.console;

public record Text(String value, Color color) {
  public static Text of(Object value, Color color) {
    return new Text(String.valueOf(value), color);
  }

  @Override
  public String toString() {
    return value;
  }
}
