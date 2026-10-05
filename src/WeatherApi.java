import java.util.LinkedHashMap;
import java.util.Map;

public class WeatherApi extends Api {
  private final AppSettings settings;
  private final String token;

  public WeatherApi(AppSettings settings, String token) {
    super("api.weather.yandex.ru", "v2", "forecast");
    this.settings = settings;
    this.token = token;
  }

  @Override
  protected Map<String, String> query() {
    Map<String, String> q = new LinkedHashMap<>();
    q.put("lat", String.valueOf(settings.latitude()));
    q.put("lon", String.valueOf(settings.longitude()));
    q.put("limit", String.valueOf(settings.limit()));
    q.put("hours", "false");
    return q;
  }

  @Override
  protected Map<String, String> headers() {
    return Map.of("X-Yandex-Weather-Key", token);
  }
}
