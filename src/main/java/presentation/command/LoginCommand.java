package presentation.command;

import domain.user.User;

public final class LoginCommand extends SignInCommand {
  public LoginCommand(FinanceContext context) {
    super(context, "login", "Войти и загрузить кошелёк");
  }

  @Override
  protected User authenticate(String login, String password) {
    return context.auth().login(login, password);
  }

  @Override
  protected String greeting(User user) {
    return "С возвращением, " + user.login();
  }
}
