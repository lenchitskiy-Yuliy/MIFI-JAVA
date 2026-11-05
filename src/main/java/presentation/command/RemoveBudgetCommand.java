package presentation.command;

import domain.wallet.Wallet;
import presentation.console.Arguments;

public final class RemoveBudgetCommand extends WalletCommand {
  public RemoveBudgetCommand(FinanceContext context) {
    super(context, "remove-budget", "<категория>", "Снять бюджет с категории");
  }

  @Override
  protected void change(Wallet wallet, Arguments args) {
    wallet.removeBudget(args.get(0));
    terminal().success("Бюджет " + wallet.category(args.get(0)) + " снят");
  }
}
