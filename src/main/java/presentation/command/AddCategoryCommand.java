package presentation.command;

import domain.wallet.Wallet;
import presentation.console.Arguments;

public final class AddCategoryCommand extends WalletCommand {
  public AddCategoryCommand(FinanceContext context) {
    super(context, "add-category", "<название>", "Создать категорию");
  }

  @Override
  protected void change(Wallet wallet, Arguments args) {
    terminal().success("Категория " + wallet.addCategory(args.get(0)) + " создана");
  }
}
