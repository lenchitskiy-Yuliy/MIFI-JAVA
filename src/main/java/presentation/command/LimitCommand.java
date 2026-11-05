package presentation.command;

import domain.link.ShortLink;
import presentation.console.Arguments;

public final class LimitCommand extends LinkCommand {
  public LimitCommand(LinkContext context) {
    super(context, "limit", "<ссылка> <лимит>", "Изменить лимит переходов");
  }

  @Override
  protected void perform(Arguments args) {
    ShortLink link = links().changeLimit(user(), code(args), args.integer(1));
    terminal().success("Лимит " + format().of(link) + ": " + link.clicks() + " / " + link.limit());
  }
}
