package presentation.command;

import domain.wallet.BudgetRule;
import domain.wallet.Money;
import presentation.console.Color;
import presentation.console.Text;

final class MoneyText {
  private MoneyText() {}

  static Text income(Money money) {
    return Text.of(money, Color.GREEN);
  }

  static Text expense(Money money) {
    return Text.of(money, Color.RED);
  }

  static Text signed(Money money) {
    return Text.of(money, money.isNegative() ? Color.RED : Color.GREEN);
  }

  static Text remaining(Money limit, Money spent) {
    Money remaining = limit.minus(spent);

    if (remaining.isNegative()) {
      return Text.of(remaining, Color.RED);
    }

    return Text.of(
        remaining,
        spent.percentOf(limit) >= BudgetRule.WARNING_PERCENT ? Color.YELLOW : Color.GREEN);
  }
}
