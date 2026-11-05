package presentation.command;

import domain.wallet.Money;
import domain.wallet.Wallet;
import presentation.console.Arguments;

public final class BudgetCommand extends WalletCommand {
  public BudgetCommand(FinanceContext context) {
    super(context, "budget", "<категория> <сумма>", "Установить или изменить бюджет");
  }

  @Override
  protected void change(Wallet wallet, Arguments args) {
    Money limit = args.get(1, Money::parse);

    wallet.setBudget(args.get(0), limit);
    terminal().success("Бюджет " + wallet.category(args.get(0)) + ": " + limit);
  }
}
