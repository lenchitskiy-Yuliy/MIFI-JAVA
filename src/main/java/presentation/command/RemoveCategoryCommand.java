package presentation.command;

import domain.wallet.Wallet;
import presentation.console.Arguments;

public final class RemoveCategoryCommand extends WalletCommand {
  public RemoveCategoryCommand(FinanceContext context) {
    super(context, "remove-category", "<название>", "Удалить категорию без операций");
  }

  @Override
  protected void change(Wallet wallet, Arguments args) {
    wallet.removeCategory(args.get(0));
    terminal().success("Категория " + args.get(0) + " удалена");
  }
}
