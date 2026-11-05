package domain.wallet;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Report {
  private final List<Category> categories;
  private final List<Operation> operations;
  private final LocalDate from;
  private final LocalDate to;
  private final boolean filtered;

  public Report(Wallet wallet, List<String> names, LocalDate from, LocalDate to) {
    if (from != null && to != null && from.isAfter(to)) {
      throw new IllegalArgumentException("Начало периода позже его конца");
    }

    this.categories =
        names.isEmpty()
            ? wallet.categories()
            : names.stream().map(wallet::category).distinct().toList();
    this.operations =
        wallet.operations().stream()
            .filter(operation -> categories.contains(operation.category()))
            .filter(operation -> operation.isIn(from, to))
            .toList();
    this.from = from;
    this.to = to;
    this.filtered = !names.isEmpty();
  }

  public static Report of(Wallet wallet) {
    return new Report(wallet, List.of(), null, null);
  }

  public Money total(OperationType type) {
    return Wallet.sum(operations.stream().filter(operation -> operation.is(type)).toList());
  }

  public Map<Category, Money> byCategory(OperationType type) {
    Map<Category, Money> totals = new LinkedHashMap<>();

    for (Category category : categories) {
      Money total = total(type, category);

      if (!total.isZero()) {
        totals.put(category, total);
      }
    }

    return totals;
  }

  public Money spent(Category category) {
    return total(OperationType.EXPENSE, category);
  }

  public List<Category> budgets() {
    return categories.stream().filter(category -> category.budget().isPresent()).toList();
  }

  public boolean isEmpty() {
    return operations.isEmpty();
  }

  public String title() {
    String scope = filtered ? "Категории: " + String.join(", ", names()) : "Все категории";

    if (from == null && to == null) {
      return scope + ", за всё время";
    }

    return scope + ", с " + from + " по " + to;
  }

  private List<String> names() {
    return categories.stream().map(Category::name).toList();
  }

  private Money total(OperationType type, Category category) {
    return Wallet.sum(
        operations.stream()
            .filter(operation -> operation.is(type) && operation.category() == category)
            .toList());
  }
}
