package presentation.command;

import domain.link.ShortLink;
import presentation.console.Arguments;

public final class EditCommand extends LinkCommand {
  public EditCommand(LinkContext context) {
    super(context, "edit", "<ссылка> <адрес>", "Поменять адрес, на который ведёт ссылка");
  }

  @Override
  protected void perform(Arguments args) {
    ShortLink link = links().changeUrl(user(), code(args), format().requireLong(args.get(1)));
    terminal().success(format().of(link) + " теперь ведёт на " + link.url());
  }
}
