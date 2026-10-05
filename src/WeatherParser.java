import java.util.List;

public class WeatherParser {
  public int getFactTemp(String json) {
    return new Json(json)
        .object("fact")
        .number("temp");
  }

  public double getMediumTempAvg(String json, int limit) {
    List<Json> forecasts = new Json(json)
        .array("forecasts")
        .objects();

    if (forecasts.size() < limit) {
      throw new IllegalArgumentException("Нет прогноза на " + limit + " дней");
    }

    int sum = 0;
    for (int i = 0; i < limit; i++) {
      sum += forecasts.get(i)
          .object("parts")
          .object("day")
          .number("temp_avg");
    }
    return (double) sum / limit;
  }
}
