package domain.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AlertsTest {
  private final Alerts alerts = Alerts.standard();
  private Wallet wallet;

  @BeforeEach
  void setUp() {
    wallet = new Wallet("alice");
    wallet.addCategory("Зарплата");
    wallet.addCategory("Еда");
    wallet.setBudget("Еда", Money.parse("1000"));
    wallet.add(OperationType.INCOME, "Зарплата", Money.parse("5000"), LocalDate.now(), "");
  }

  @Test
  void noAlertsWhileBudgetIsFar() {
    expense("500");

    assertTrue(alerts.check(wallet).isEmpty());
  }

  @Test
  void warnsNearLimit() {
    expense("850");

    assertEquals(
        List.of(new Alert("Бюджет Еда израсходован на 85%, осталось 150.00")),
        alerts.check(wallet));
  }

  @Test
  void warnsWhenBudgetExceeded() {
    expense("1200");

    assertEquals(
        List.of(new Alert("Превышен бюджет Еда: перерасход 200.00")), alerts.check(wallet));
  }

  @Test
  void warnsWhenExpensesExceedIncome() {
    wallet.removeBudget("Еда");
    expense("6000");

    assertEquals(List.of(new Alert("Расходы превысили доходы на 1 000.00")), alerts.check(wallet));
  }

  private void expense(String amount) {
    wallet.add(OperationType.EXPENSE, "Еда", Money.parse(amount), LocalDate.now(), "");
  }
}
