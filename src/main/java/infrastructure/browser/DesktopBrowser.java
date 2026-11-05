package infrastructure.browser;

import application.port.Browser;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Locale;

public final class DesktopBrowser implements Browser {
  @Override
  public void open(URI uri) {
    if (!desktop(uri) && !system(uri)) {
      throw new IllegalStateException("Не удалось открыть браузер, откройте вручную: " + uri);
    }
  }

  private static boolean desktop(URI uri) {
    try {
      if (!Desktop.isDesktopSupported()
          || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        return false;
      }

      Desktop.getDesktop().browse(uri);
      return true;
    } catch (IOException | RuntimeException exception) {
      return false;
    }
  }

  private static boolean system(URI uri) {
    try {
      new ProcessBuilder(command(uri)).start();
      return true;
    } catch (IOException | RuntimeException exception) {
      return false;
    }
  }

  private static List<String> command(URI uri) {
    String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);

    if (os.contains("win")) {
      return List.of("rundll32", "url.dll,FileProtocolHandler", uri.toString());
    }

    return List.of(os.contains("mac") ? "open" : "xdg-open", uri.toString());
  }
}
