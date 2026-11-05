package domain.wallet;

import java.util.List;

public final class Alerts {
  private final List<AlertRule> rules;

  public Alerts(List<AlertRule> rules) {
    this.rules = List.copyOf(rules);
  }

  public static Alerts standard() {
    return new Alerts(List.of(new BudgetRule(), new BalanceRule()));
  }

  public List<Alert> check(Wallet wallet) {
    return rules.stream().flatMap(rule -> rule.check(wallet).stream()).toList();
  }
}
