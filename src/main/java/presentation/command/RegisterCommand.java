package presentation.command;

import domain.user.User;

public final class RegisterCommand extends SignInCommand {
  public RegisterCommand(FinanceContext context) {
    super(context, "register", "Зарегистрироваться и войти");
  }

  @Override
  protected User authenticate(String login, String password) {
    return context.auth().register(login, password);
  }

  @Override
  protected String greeting(User user) {
    return "Пользователь " + user.login() + " зарегистрирован";
  }
}
