package domain.wallet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Wallet {
  private final String owner;
  private final List<Category> categories = new ArrayList<>();
  private final List<Operation> operations = new ArrayList<>();

  public Wallet(String owner) {
    this.owner = owner;
  }

  public String owner() {
    return owner;
  }

  public List<Category> categories() {
    return List.copyOf(categories);
  }

  public List<Operation> operations() {
    return List.copyOf(operations);
  }

  public Optional<Category> findCategory(String name) {
    return categories.stream().filter(category -> category.is(name)).findFirst();
  }

  public Category category(String name) {
    return findCategory(name)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Категория не найдена: "
                        + name
                        + ". Создайте её: add-category \""
                        + name
                        + "\""));
  }

  public Category addCategory(String name) {
    if (findCategory(name).isPresent()) {
      throw new IllegalArgumentException("Категория уже существует: " + name);
    }

    Category category = new Category(name);
    categories.add(category);
    return category;
  }

  public void renameCategory(String name, String newName) {
    Category category = category(name);

    findCategory(newName)
        .filter(other -> other != category)
        .ifPresent(
            other -> {
              throw new IllegalArgumentException("Категория уже существует: " + newName);
            });

    category.rename(newName);
  }

  public void removeCategory(String name) {
    Category category = category(name);

    if (operations.stream().anyMatch(operation -> operation.category() == category)) {
      throw new IllegalArgumentException(
          "В категории " + category + " есть операции, её нельзя удалить");
    }

    categories.remove(category);
  }

  public void setBudget(String name, Money limit) {
    category(name).setBudget(limit);
  }

  public void removeBudget(String name) {
    category(name).setBudget(null);
  }

  public Operation add(
      OperationType type, String category, Money amount, LocalDate date, String note) {
    Operation operation = new Operation(type, category(category), amount, date, note);
    operations.add(operation);
    return operation;
  }

  public Money total(OperationType type) {
    return sum(operations.stream().filter(operation -> operation.is(type)).toList());
  }

  public Money balance() {
    return total(OperationType.INCOME).minus(total(OperationType.EXPENSE));
  }

  public Money spent(Category category) {
    return sum(
        operations.stream()
            .filter(operation -> operation.is(OperationType.EXPENSE))
            .filter(operation -> operation.category() == category)
            .toList());
  }

  static Money sum(List<Operation> operations) {
    return operations.stream().map(Operation::amount).reduce(Money.ZERO, Money::plus);
  }
}
