package presentation.command;

import domain.user.User;
import domain.wallet.Wallet;
import presentation.console.Arguments;

public abstract class SignInCommand extends FinanceCommand {
  protected SignInCommand(FinanceContext context, String name, String description) {
    super(context, name, "<логин> <пароль>", description);
  }

  @Override
  protected final void execute(Arguments args) {
    if (context.session().isActive()) {
      throw new IllegalStateException("Сначала выйдите из аккаунта: logout");
    }

    User user = authenticate(args.get(0), args.get(1));
    Wallet wallet = context.wallets().open(user.login());
    context.session().start(user, wallet);

    terminal().success(greeting(user));
    terminal().println("Баланс: " + terminal().paint(MoneyText.signed(wallet.balance())));
    warn(context.alerts().check(wallet));
  }

  protected abstract User authenticate(String login, String password);

  protected abstract String greeting(User user);
}
