package domain.wallet;

import java.time.LocalDate;

final class TaskExample {
  static final LocalDate FIRST = LocalDate.of(2026, 11, 1);
  static final LocalDate SECOND = LocalDate.of(2026, 11, 20);

  private TaskExample() {}

  static Wallet wallet() {
    Wallet wallet = new Wallet("alice");

    for (String name : new String[] {"Еда", "Развлечения", "Коммунальные услуги", "Такси"}) {
      wallet.addCategory(name);
    }

    wallet.addCategory("Зарплата");
    wallet.addCategory("Бонус");

    income(wallet, "Зарплата", "20000", FIRST);
    income(wallet, "Зарплата", "40000", SECOND);
    income(wallet, "Бонус", "3000", SECOND);

    expense(wallet, "Еда", "300", FIRST);
    expense(wallet, "Еда", "500", SECOND);
    expense(wallet, "Развлечения", "3000", SECOND);
    expense(wallet, "Коммунальные услуги", "3000", SECOND);
    expense(wallet, "Такси", "1500", SECOND);

    wallet.setBudget("Еда", Money.parse("4000"));
    wallet.setBudget("Развлечения", Money.parse("3000"));
    wallet.setBudget("Коммунальные услуги", Money.parse("2500"));
    return wallet;
  }

  private static void income(Wallet wallet, String category, String amount, LocalDate date) {
    wallet.add(OperationType.INCOME, category, Money.parse(amount), date, "");
  }

  private static void expense(Wallet wallet, String category, String amount, LocalDate date) {
    wallet.add(OperationType.EXPENSE, category, Money.parse(amount), date, "");
  }
}
