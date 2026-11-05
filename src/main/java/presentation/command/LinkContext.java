package presentation.command;

import application.LinkService;
import application.Session;
import application.port.Browser;
import presentation.console.Terminal;

public record LinkContext(
    Session session, LinkService links, Browser browser, LinkFormat format, Terminal terminal) {
  public void deliverNotifications() {
    session
        .user()
        .ifPresent(
            user ->
                links.notifications(user).forEach(item -> terminal.warning(format.notice(item))));
  }
}
