package infrastructure.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Csv {
  private Csv() {}

  public static String line(Object... values) {
    return Arrays.stream(values).map(Csv::escape).collect(Collectors.joining(","));
  }

  public static List<String> parse(String line) {
    List<String> values = new ArrayList<>();
    StringBuilder value = new StringBuilder();
    boolean quoted = false;

    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);

      if (c == '"' && quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
        value.append('"');
        i++;
      } else if (c == '"') {
        quoted = !quoted;
      } else if (c == ',' && !quoted) {
        values.add(value.toString());
        value.setLength(0);
      } else {
        value.append(c);
      }
    }

    values.add(value.toString());
    return values;
  }

  private static String escape(Object value) {
    String text = value == null ? "" : value.toString();

    if (text.contains(",") || text.contains("\"")) {
      return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    return text;
  }
}
