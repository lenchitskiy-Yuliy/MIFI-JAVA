package presentation.command;

import domain.link.LinkStatus;
import domain.link.ShortLink;
import java.util.List;
import presentation.console.Arguments;
import presentation.console.Color;
import presentation.console.Table;
import presentation.console.Text;

public final class LinksCommand extends LinkCommand {
  private static final int URL_WIDTH = 40;

  public LinksCommand(LinkContext context) {
    super(context, "links", "", "Мои ссылки");
  }

  @Override
  protected void perform(Arguments args) {
    List<ShortLink> list = links().of(user());

    if (list.isEmpty()) {
      terminal().hint("Ссылок нет. Создать: create <адрес> [лимит]");
      return;
    }

    Table table = new Table("Ссылка", "Адрес", "Переходы", "Работает до", "Статус").alignRight(2);

    for (ShortLink link : list) {
      table.row(
          format().of(link),
          cut(link.url().toString()),
          link.clicks() + " / " + link.limit(),
          format().time(link.expiresAt()),
          status(link));
    }

    table.print(terminal());
  }

  private static Text status(ShortLink link) {
    return link.isExhausted()
        ? Text.of(LinkStatus.EXHAUSTED.title(), Color.RED)
        : Text.of(LinkStatus.ACTIVE.title(), Color.GREEN);
  }

  private static String cut(String url) {
    return url.length() > URL_WIDTH ? url.substring(0, URL_WIDTH - 3) + "..." : url;
  }
}
