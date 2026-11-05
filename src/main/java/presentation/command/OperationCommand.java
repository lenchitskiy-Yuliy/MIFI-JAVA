package presentation.command;

import domain.wallet.Money;
import domain.wallet.Operation;
import domain.wallet.OperationType;
import domain.wallet.Wallet;
import java.time.LocalDate;
import presentation.console.Arguments;

public final class OperationCommand extends WalletCommand {
  private final OperationType type;

  public OperationCommand(FinanceContext context, OperationType type) {
    super(
        context,
        type == OperationType.INCOME ? "income" : "expense",
        "<категория> <сумма> [дата] [комментарий]",
        type == OperationType.INCOME ? "Добавить доход" : "Добавить расход");
    this.type = type;
  }

  @Override
  protected void change(Wallet wallet, Arguments args) {
    Operation operation =
        wallet.add(
            type,
            args.get(0),
            args.get(1, Money::parse),
            args.date(2, LocalDate.now()),
            args.has(3) ? args.get(3) : "");

    terminal()
        .success(
            (type == OperationType.INCOME ? "Доход " : "Расход ")
                + operation.amount()
                + " в категории "
                + operation.category()
                + ", баланс "
                + wallet.balance());
  }
}
