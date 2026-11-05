package presentation.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class Table {
  private final String[] headers;
  private final List<Object[]> rows = new ArrayList<>();
  private final Set<Integer> right = new HashSet<>();

  public Table(String... headers) {
    this.headers = headers;
  }

  public Table alignRight(int... columns) {
    Arrays.stream(columns).forEach(right::add);
    return this;
  }

  public Table row(Object... cells) {
    rows.add(cells);
    return this;
  }

  public void print(Terminal terminal) {
    int[] widths = widths();

    terminal.println(line(terminal, widths, boldHeaders()));
    terminal.println(rule(widths));
    rows.forEach(row -> terminal.println(line(terminal, widths, row)));
  }

  private int[] widths() {
    int[] widths = Arrays.stream(headers).mapToInt(String::length).toArray();
    for (Object[] row : rows) {
      for (int i = 0; i < row.length; i++) {
        widths[i] = Math.max(widths[i], String.valueOf(row[i]).length());
      }
    }

    return widths;
  }

  private Object[] boldHeaders() {
    return Arrays.stream(headers).map(header -> Text.of(header, Color.BOLD)).toArray();
  }

  private String line(Terminal terminal, int[] widths, Object[] cells) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < widths.length; i++) {
      Object cell = i < cells.length ? cells[i] : "";
      String padding = " ".repeat(widths[i] - String.valueOf(cell).length());
      String value = terminal.paint(cell);

      sb.append(i == 0 ? "" : "  ").append(right.contains(i) ? padding + value : value + padding);
    }

    return sb.toString().stripTrailing();
  }

  private static String rule(int[] widths) {
    return Arrays.stream(widths).mapToObj("-"::repeat).collect(Collectors.joining("  "));
  }
}
