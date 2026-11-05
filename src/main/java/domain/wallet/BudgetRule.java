package domain.wallet;

import java.util.ArrayList;
import java.util.List;

public final class BudgetRule implements AlertRule {
  public static final int WARNING_PERCENT = 80;

  @Override
  public List<Alert> check(Wallet wallet) {
    List<Alert> alerts = new ArrayList<>();

    for (Category category : wallet.categories()) {
      if (category.budget().isEmpty()) {
        continue;
      }

      Money limit = category.budget().get();
      Money spent = wallet.spent(category);
      int percent = spent.percentOf(limit);

      if (spent.compareTo(limit) > 0) {
        alerts.add(new Alert("Превышен бюджет " + category + ": перерасход " + spent.minus(limit)));
      } else if (percent >= WARNING_PERCENT) {
        alerts.add(
            new Alert(
                "Бюджет "
                    + category
                    + " израсходован на "
                    + percent
                    + "%, осталось "
                    + limit.minus(spent)));
      }
    }

    return alerts;
  }
}
