package presentation.command;

import java.util.UUID;
import presentation.console.Arguments;

public final class UserCommand extends LinkCommand {
  public UserCommand(LinkContext context) {
    super(context, "user", "[uuid]", "Показать свой UUID или вернуться к своим ссылкам по UUID");
  }

  @Override
  protected void perform(Arguments args) {
    if (!args.has(0)) {
      terminal().println("Ваш UUID: " + user());
      return;
    }

    UUID user = parse(args.get(0));

    if (!links().knows(user)) {
      throw new IllegalArgumentException("У пользователя " + user + " нет ссылок");
    }

    context.session().use(user);
    terminal().success("Вы вошли как " + user + ", ссылок: " + links().of(user).size());
  }

  private static UUID parse(String value) {
    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException exception) {
      throw new IllegalArgumentException("Некорректный UUID: " + value);
    }
  }
}
