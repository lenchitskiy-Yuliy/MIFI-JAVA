package presentation.command;

import domain.wallet.Alert;
import domain.wallet.Wallet;
import java.util.List;
import presentation.console.Arguments;

public abstract class WalletCommand extends FinanceCommand {
  protected WalletCommand(FinanceContext context, String name, String params, String description) {
    super(context, name, params, description);
  }

  @Override
  protected final void execute(Arguments args) {
    Wallet wallet = wallet();
    List<Alert> before = context.alerts().check(wallet);

    change(wallet, args);

    warn(context.alerts().check(wallet).stream().filter(alert -> !before.contains(alert)).toList());
  }

  protected abstract void change(Wallet wallet, Arguments args);
}
