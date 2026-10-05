import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public abstract class Api {
  private static final HttpClient CLIENT = HttpClient.newHttpClient();

  private final String domain;
  private final String version;
  private final String path;

  public Api(String domain, String version, String path) {
    this.domain = domain;
    this.version = version;
    this.path = path;
  }

  protected abstract Map<String, String> query();

  protected abstract Map<String, String> headers();

  public final String fetch() throws IOException, InterruptedException {
    HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(getPath(query())))
        .GET();

    headers().forEach(builder::header);

    HttpRequest request = builder.build();

    HttpResponse<String> response = CLIENT.send(
        request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() < 200 || response.statusCode() >= 300) {
      throw new IOException("API вернуло HTTP " + response.statusCode() + ": " + response.body());
    }

    return response.body();
  }

  public String getPath(Map<String, String> query) {
    String base = "https://" + domain + "/" + version + "/" + path;
    if (query == null || query.isEmpty()) {
      return base;
    }

    StringBuilder sb = new StringBuilder(base).append('?');

    boolean first = true;

    for (Map.Entry<String, String> e : query.entrySet()) {
      if (!first) {
        sb.append('&');
      }
      first = false;

      sb.append(encode(e.getKey())).append('=').append(encode(e.getValue()));
    }

    return sb.toString();
  }

  private static String encode(String value) {
    return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
  }
}
