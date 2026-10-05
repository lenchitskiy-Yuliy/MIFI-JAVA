import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Json {
  private final String json;

  public Json(String json) {
    this.json = json;
  }

  public Json object(String field) {
    return new Json(value(field, '{', '}'));
  }

  public Json array(String field) {
    return new Json(value(field, '[', ']'));
  }

  public int number(String field) {
    Matcher m = Pattern.compile("\\\"" + Pattern.quote(field) + "\\\"\\s*:\\s*(-?\\d+)").matcher(json);

    if (!m.find()) {
      throw new IllegalArgumentException("Не найдено числовое поле " + field);
    }

    return Integer.parseInt(m.group(1));
  }

  public List<Json> objects() {
    List<Json> result = new ArrayList<>();

    for (int i = 0; i < json.length(); i++) {
      if (json.charAt(i) == '{') {
        int end = closingBracket(json, i, '{', '}');

        result.add(new Json(json.substring(i, end + 1)));
        i = end;
      }
    }

    return result;
  }

  private String value(String field, char opening, char closing) {
    Matcher m = Pattern.compile("\\\"" + Pattern.quote(field) + "\\\"\\s*:").matcher(json);

    if (!m.find()) {
      throw new IllegalArgumentException("Не найдено поле " + field);
    }

    int start = m.end();

    while (Character.isWhitespace(json.charAt(start))) {
      start++;
    }

    if (json.charAt(start) != opening) {
      throw new IllegalArgumentException("Поле " + field + " имеет неверный формат");
    }

    return json.substring(start, closingBracket(json, start, opening, closing) + 1);
  }

  private static int closingBracket(String json, int start, char opening, char closing) {
    int depth = 0;
    boolean string = false;

    for (int i = start; i < json.length(); i++) {
      char c = json.charAt(i);

      if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
        string = !string;
      }

      if (!string && c == opening) {
        depth++;
      } else if (!string && c == closing && --depth == 0) {
        return i;
      }
    }

    throw new IllegalArgumentException("Некорректный JSON формат");
  }
}
