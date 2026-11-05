package presentation.command;

import application.LinkService;
import java.util.UUID;
import presentation.console.Arguments;
import presentation.console.Command;
import presentation.console.Terminal;

public abstract class LinkCommand extends Command {
  protected final LinkContext context;

  protected LinkCommand(LinkContext context, String name, String params, String description) {
    super(name, params, description);
    this.context = context;
  }

  @Override
  protected final void execute(Arguments args) {
    try {
      perform(args);
    } finally {
      context.deliverNotifications();
    }
  }

  protected abstract void perform(Arguments args);

  protected LinkService links() {
    return context.links();
  }

  protected LinkFormat format() {
    return context.format();
  }

  protected Terminal terminal() {
    return context.terminal();
  }

  protected UUID user() {
    return context.session().require();
  }

  protected String code(Arguments args) {
    return format().code(args.get(0));
  }
}
