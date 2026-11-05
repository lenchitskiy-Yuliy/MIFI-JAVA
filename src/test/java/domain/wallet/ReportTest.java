package domain.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReportTest {
  private final Wallet wallet = TaskExample.wallet();

  @Test
  void matchesTaskExample() {
    Report report = Report.of(wallet);

    assertEquals(Money.parse("63000"), report.total(OperationType.INCOME));
    assertEquals(Money.parse("8300"), report.total(OperationType.EXPENSE));
    assertEquals(
        Money.parse("60000"),
        report.byCategory(OperationType.INCOME).get(wallet.category("Зарплата")));
    assertEquals("-500.00", remaining("Коммунальные услуги"));
    assertEquals("3 200.00", remaining("Еда"));
    assertEquals("0.00", remaining("Развлечения"));
  }

  @Test
  void countsSelectedCategories() {
    Report report = new Report(wallet, List.of("Еда", "такси"), null, null);

    assertEquals(Money.parse("2300"), report.total(OperationType.EXPENSE));
    assertEquals(Money.ZERO, report.total(OperationType.INCOME));
  }

  @Test
  void filtersByPeriod() {
    Report report = new Report(wallet, List.of(), TaskExample.FIRST, TaskExample.FIRST);

    assertEquals(Money.parse("20000"), report.total(OperationType.INCOME));
    assertEquals(Money.parse("300"), report.total(OperationType.EXPENSE));
  }

  @Test
  void emptyPeriodAndUnknownCategory() {
    assertTrue(new Report(wallet, List.of(), TaskExample.SECOND.plusDays(1), null).isEmpty());
    assertThrows(
        IllegalArgumentException.class, () -> new Report(wallet, List.of("Кино"), null, null));
    assertThrows(
        IllegalArgumentException.class,
        () -> new Report(wallet, List.of(), TaskExample.SECOND, TaskExample.FIRST));
  }

  private String remaining(String name) {
    Category category = wallet.category(name);
    return category.budget().orElseThrow().minus(Report.of(wallet).spent(category)).toString();
  }
}
