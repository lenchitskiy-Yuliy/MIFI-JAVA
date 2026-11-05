package presentation.command;

import domain.wallet.Category;
import domain.wallet.Money;
import domain.wallet.OperationType;
import domain.wallet.Report;
import java.util.Map;
import presentation.console.Table;
import presentation.console.Terminal;

final class ReportView {
  private final Terminal terminal;

  ReportView(Terminal terminal) {
    this.terminal = terminal;
  }

  void print(Report report) {
    terminal.title(report.title());

    if (report.isEmpty()) {
      terminal.hint("Нет операций по выбранным условиям");
    } else {
      totals(report, OperationType.INCOME);
      totals(report, OperationType.EXPENSE);

      Money difference =
          report.total(OperationType.INCOME).minus(report.total(OperationType.EXPENSE));
      terminal.println("Доходы минус расходы: " + terminal.paint(MoneyText.signed(difference)));
    }

    budgets(report);
  }

  private void totals(Report report, OperationType type) {
    Map<Category, Money> totals = report.byCategory(type);

    if (totals.isEmpty()) {
      return;
    }

    Money total = report.total(type);
    Table table = new Table(type.title(), "Сумма").alignRight(1);

    totals.forEach((category, amount) -> table.row(category, amount));
    table.footer(
        "Итого", type == OperationType.INCOME ? MoneyText.income(total) : MoneyText.expense(total));
    table.print(terminal);
  }

  private void budgets(Report report) {
    if (report.budgets().isEmpty()) {
      return;
    }

    Table table = new Table("Бюджет", "Лимит", "Потрачено", "Остаток").alignRight(1, 2, 3);

    for (Category category : report.budgets()) {
      Money limit = category.budget().orElseThrow();
      Money spent = report.spent(category);

      table.row(category, limit, spent, MoneyText.remaining(limit, spent));
    }

    table.print(terminal);
  }
}
