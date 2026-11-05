package domain.wallet;

import java.util.List;

public final class BalanceRule implements AlertRule {
  @Override
  public List<Alert> check(Wallet wallet) {
    Money balance = wallet.balance();

    if (balance.isNegative()) {
      return List.of(new Alert("Расходы превысили доходы на " + Money.ZERO.minus(balance)));
    }

    if (balance.isZero() && !wallet.operations().isEmpty()) {
      return List.of(new Alert("Баланс кошелька равен нулю"));
    }

    return List.of();
  }
}
