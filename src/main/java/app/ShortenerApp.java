package app;

import application.LinkService;
import application.Session;
import application.port.Browser;
import domain.link.ShortLink;
import infrastructure.code.RandomCodeGenerator;
import infrastructure.storage.FileInbox;
import infrastructure.storage.FileLinkRepository;
import infrastructure.storage.FileUserStore;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import presentation.command.CreateCommand;
import presentation.command.DeleteCommand;
import presentation.command.EditCommand;
import presentation.command.LimitCommand;
import presentation.command.LinkContext;
import presentation.command.LinkFormat;
import presentation.command.LinksCommand;
import presentation.command.LogoutCommand;
import presentation.command.OpenCommand;
import presentation.command.UserCommand;
import presentation.console.Console;
import presentation.console.Terminal;

public final class ShortenerApp {
  private final AppConfig config;
  private final Terminal terminal;
  private final LinkService links;
  private final LinkContext context;
  private final Console console;

  public ShortenerApp(AppConfig config, Terminal terminal, Browser browser, Clock clock) {
    this.config = config;
    this.terminal = terminal;

    Path data = config.dataDirectory();
    links =
        new LinkService(
            new FileLinkRepository(data.resolve("links.csv")),
            new RandomCodeGenerator(config.codeLength()),
            new FileInbox(data.resolve("notifications.csv")),
            config.links(),
            clock);

    Session session = new Session(new FileUserStore(data.resolve("user.txt")));
    context = new LinkContext(session, links, browser, new LinkFormat(config.domain()), terminal);

    console =
        new Console(
                terminal,
                () -> session.user().map(user -> user.toString().substring(0, 8)).orElse("guest"),
                List.of(
                    new CreateCommand(context),
                    new OpenCommand(context),
                    new LinksCommand(context),
                    new LimitCommand(context),
                    new EditCommand(context),
                    new DeleteCommand(context),
                    new UserCommand(context),
                    new LogoutCommand(context)))
            .hint("Пример: create https://www.baeldung.com/java-9-http-client 5")
            .hint("Короткую ссылку можно просто вставить в консоль: " + config.domain() + "/3DZHeG")
            .hint("Если в адресе есть пробелы, возьмите его в кавычки");
  }

  public void run(BufferedReader input) throws IOException {
    ScheduledExecutorService cleaner = startCleaner();

    terminal.title("Сокращатель ссылок");
    terminal.hint("help покажет команды, exit для выхода");
    terminal.hint("Ссылки живут " + context.format().duration(config.links().ttl()));
    context.deliverNotifications();

    try {
      console.run(input);
    } finally {
      cleaner.shutdownNow();
    }
  }

  private ScheduledExecutorService startCleaner() {
    ScheduledExecutorService cleaner =
        Executors.newSingleThreadScheduledExecutor(
            task -> {
              Thread thread = new Thread(task, "link-cleaner");
              thread.setDaemon(true);
              return thread;
            });
    long period = config.cleanupInterval().toMillis();

    cleaner.scheduleAtFixedRate(this::cleanUp, period, period, TimeUnit.MILLISECONDS);
    return cleaner;
  }

  private boolean isCurrent(UUID user) {
    return context.session().user().map(user::equals).orElse(false);
  }

  private void cleanUp() {
    try {
      if (links.removeExpired().stream().map(ShortLink::owner).anyMatch(this::isCurrent)) {
        terminal.println();
        context.deliverNotifications();
      }
    } catch (RuntimeException exception) {
      terminal.error("Не удалось удалить устаревшие ссылки: " + exception.getMessage());
    }
  }
}
