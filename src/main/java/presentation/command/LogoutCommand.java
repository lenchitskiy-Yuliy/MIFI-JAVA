package presentation.command;

import presentation.console.Arguments;

public final class LogoutCommand extends LinkCommand {
  public LogoutCommand(LinkContext context) {
    super(context, "logout", "", "Забыть UUID, следующая ссылка создаст нового пользователя");
  }

  @Override
  protected void perform(Arguments args) {
    user();
    context.session().forget();
    terminal().success("UUID забыт");
  }
}
