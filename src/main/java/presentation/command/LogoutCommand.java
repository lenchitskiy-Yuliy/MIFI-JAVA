package presentation.command;

import presentation.console.Arguments;

public final class LogoutCommand extends FinanceCommand {
  public LogoutCommand(FinanceContext context) {
    super(context, "logout", "", "Сохранить кошелёк и выйти из аккаунта");
  }

  @Override
  protected void execute(Arguments args) {
    String login = context.session().user().login();

    context.wallets().save(wallet());
    context.session().end();
    terminal().success("Кошелёк сохранён, до встречи, " + login);
  }
}
