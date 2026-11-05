package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.LinkSettings;
import application.TestClock;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import presentation.console.Terminal;

class ShortenerAppTest {
  @TempDir Path data;

  private final TestClock clock = new TestClock();
  private final List<URI> opened = new ArrayList<>();

  @Test
  void opensLinkUntilLimitAndTellsOwner() throws Exception {
    String created = run("create https://ru.stackoverflow.com 1\n");
    String link = find("Короткая ссылка: (mifi\\.test/\\w+)", created);

    String output = run(link + "\n" + link + "\nlinks\n");

    assertEquals(List.of(URI.create("https://ru.stackoverflow.com")), opened);
    assertTrue(output.contains("Ссылка недоступна: лимит переходов исчерпан"));
    assertTrue(output.contains("больше не работает: лимит переходов исчерпан"));
    assertTrue(output.contains("1 / 1"));
  }

  @Test
  void otherUserCannotTouchLink() throws Exception {
    String created = run("create https://example.com\n");
    String owner = find("Создан пользователь ([0-9a-f-]+)", created);
    String link = find("Короткая ссылка: (mifi\\.test/\\w+)", created);

    String output =
        run(
            "logout\ncreate https://example.com\ndelete "
                + link
                + "\nuser "
                + owner
                + "\ndelete "
                + link
                + "\n");

    assertTrue(output.contains("Это чужая ссылка"));
    assertTrue(output.contains("Ссылка " + link + " удалена"));
  }

  @Test
  void tellsAboutExpiredLinkOnNextStart() throws Exception {
    run("create https://example.com\n");
    clock.advance(Duration.ofDays(2));

    String output = run("links\n");

    assertTrue(output.contains("срок жизни истёк, она удалена"));
    assertTrue(output.contains("Ссылок нет"));
  }

  private String run(String script) throws Exception {
    StringWriter output = new StringWriter();
    AppConfig config =
        new AppConfig(
            data,
            "mifi.test",
            6,
            new LinkSettings(Duration.ofDays(1), 10, 100),
            Duration.ofHours(1));

    new ShortenerApp(config, new Terminal(new PrintWriter(output, true), false), opened::add, clock)
        .run(new BufferedReader(new StringReader(script)));
    return output.toString();
  }

  private static String find(String regex, String text) {
    Matcher matcher = Pattern.compile(regex).matcher(text);
    assertTrue(matcher.find(), text);
    return matcher.group(1);
  }
}
