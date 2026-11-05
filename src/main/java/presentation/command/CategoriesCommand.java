package presentation.command;

import domain.wallet.Category;
import domain.wallet.Money;
import domain.wallet.Wallet;
import presentation.console.Arguments;
import presentation.console.Table;

public final class CategoriesCommand extends FinanceCommand {
  public CategoriesCommand(FinanceContext context) {
    super(context, "categories", "", "Категории и бюджеты");
  }

  @Override
  protected void execute(Arguments args) {
    Wallet wallet = wallet();

    if (wallet.categories().isEmpty()) {
      terminal().hint("Категорий пока нет. Создайте первую: add-category Еда");
      return;
    }

    Table table = new Table("Категория", "Бюджет", "Потрачено", "Остаток").alignRight(1, 2, 3);

    for (Category category : wallet.categories()) {
      Money spent = wallet.spent(category);
      Money limit = category.budget().orElse(null);

      table.row(
          category,
          limit == null ? "" : limit,
          spent,
          limit == null ? "" : MoneyText.remaining(limit, spent));
    }

    table.print(terminal());
  }
}
