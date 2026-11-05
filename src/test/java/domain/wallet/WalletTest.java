package domain.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletTest {
  private Wallet wallet;

  @BeforeEach
  void setUp() {
    wallet = new Wallet("alice");
    wallet.addCategory("Еда");
  }

  @Test
  void categoryNamesAreCaseInsensitive() {
    assertThrows(IllegalArgumentException.class, () -> wallet.addCategory("еда"));
    assertEquals("Еда", wallet.category(" ЕДА ").name());
  }

  @Test
  void renameKeepsOperationsAndBudget() {
    wallet.setBudget("Еда", Money.parse("1000"));
    wallet.add(OperationType.EXPENSE, "Еда", Money.parse("300"), LocalDate.now(), "");

    wallet.renameCategory("Еда", "Продукты");

    Category category = wallet.category("Продукты");
    assertEquals(Money.parse("300"), wallet.spent(category));
    assertEquals(Money.parse("1000"), category.budget().orElseThrow());
  }

  @Test
  void usedCategoryCannotBeRemoved() {
    wallet.add(OperationType.EXPENSE, "Еда", Money.parse("300"), LocalDate.now(), "");

    assertThrows(IllegalArgumentException.class, () -> wallet.removeCategory("Еда"));
  }

  @Test
  void unknownCategoryIsReported() {
    IllegalArgumentException error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                wallet.add(OperationType.EXPENSE, "Такси", Money.parse("1"), LocalDate.now(), ""));

    assertEquals(true, error.getMessage().startsWith("Категория не найдена: Такси"));
  }
}
