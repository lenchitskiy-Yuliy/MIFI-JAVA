package presentation.command;

import domain.wallet.Money;
import domain.wallet.Wallet;
import java.time.LocalDate;
import presentation.console.Arguments;

public final class TransferCommand extends WalletCommand {
  public TransferCommand(FinanceContext context) {
    super(context, "transfer", "<логин> <сумма>", "Перевести деньги другому пользователю");
  }

  @Override
  protected void change(Wallet wallet, Arguments args) {
    Money amount = args.get(1, Money::parse);

    context.wallets().transfer(wallet, args.get(0), amount, LocalDate.now());
    terminal().success("Переведено " + amount + " пользователю " + args.get(0));
  }
}
