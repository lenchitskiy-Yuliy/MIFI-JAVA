package presentation.command;

import domain.wallet.Wallet;
import presentation.console.Arguments;

public final class RenameCategoryCommand extends WalletCommand {
  public RenameCategoryCommand(FinanceContext context) {
    super(context, "rename-category", "<название> <новое>", "Переименовать категорию");
  }

  @Override
  protected void change(Wallet wallet, Arguments args) {
    wallet.renameCategory(args.get(0), args.get(1));
    terminal().success("Категория переименована в " + wallet.category(args.get(1)));
  }
}
