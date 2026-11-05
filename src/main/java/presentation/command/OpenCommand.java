package presentation.command;

import domain.link.ShortLink;
import presentation.console.Arguments;

public final class OpenCommand extends LinkCommand {
  public OpenCommand(LinkContext context) {
    super(context, "open", "<ссылка>", "Перейти по ссылке, можно просто вставить её в консоль");
  }

  @Override
  public boolean catches(String input) {
    return format().isShort(input);
  }

  @Override
  protected void perform(Arguments args) {
    ShortLink link = links().open(code(args));

    terminal().success("Открываю " + link.url());
    terminal().hint("Осталось переходов: " + (link.limit() - link.clicks()));
    context.browser().open(link.url().value());
  }
}
