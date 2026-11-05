package presentation.command;

import domain.link.ShortLink;
import java.util.Optional;
import java.util.UUID;
import presentation.console.Arguments;

public final class CreateCommand extends LinkCommand {
  public CreateCommand(LinkContext context) {
    super(context, "create", "<адрес> [лимит]", "Сократить ссылку, лимит это число переходов");
  }

  @Override
  protected void perform(Arguments args) {
    String url = format().requireLong(args.get(0));
    Optional<UUID> current = context.session().user();
    UUID owner = current.orElseGet(UUID::randomUUID);
    ShortLink link =
        args.has(1) ? links().create(owner, url, args.integer(1)) : links().create(owner, url);

    if (current.isEmpty()) {
      context.session().use(owner);
      terminal().success("Создан пользователь " + owner);
      terminal().hint("Запомните UUID, по нему можно вернуться к своим ссылкам: user <uuid>");
    }

    terminal().success("Короткая ссылка: " + format().of(link));
    terminal()
        .hint("Переходов: " + link.limit() + ", работает до " + format().time(link.expiresAt()));
  }
}
