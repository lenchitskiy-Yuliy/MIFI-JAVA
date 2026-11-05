package presentation.command;

import domain.wallet.Operation;
import domain.wallet.OperationType;
import java.util.Comparator;
import java.util.List;
import presentation.console.Arguments;
import presentation.console.Color;
import presentation.console.Table;
import presentation.console.Text;

public final class HistoryCommand extends FinanceCommand {
  public HistoryCommand(FinanceContext context) {
    super(context, "history", "[категория...]", "История операций");
  }

  @Override
  protected void execute(Arguments args) {
    List<String> names = args.from(0);
    names.forEach(wallet()::category);

    List<Operation> operations =
        wallet().operations().stream()
            .filter(
                operation -> names.isEmpty() || names.stream().anyMatch(operation.category()::is))
            .sorted(Comparator.comparing(Operation::date))
            .toList();

    if (operations.isEmpty()) {
      terminal().hint("Операций пока нет");
      return;
    }

    Table table = new Table("Дата", "Категория", "Сумма", "Комментарий").alignRight(2);
    operations.forEach(
        operation ->
            table.row(operation.date(), operation.category(), amount(operation), operation.note()));
    table.print(terminal());
  }

  private static Text amount(Operation operation) {
    return operation.is(OperationType.INCOME)
        ? Text.of("+" + operation.amount(), Color.GREEN)
        : Text.of("-" + operation.amount(), Color.RED);
  }
}
