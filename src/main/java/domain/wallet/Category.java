package domain.wallet;

import java.util.Optional;

public final class Category {
  private static final int MAX_LENGTH = 40;

  private String name;
  private Money budget;

  Category(String name) {
    rename(name);
  }

  public String name() {
    return name;
  }

  public Optional<Money> budget() {
    return Optional.ofNullable(budget);
  }

  public boolean is(String name) {
    return this.name.equalsIgnoreCase(name.strip());
  }

  void rename(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Название категории не может быть пустым");
    }

    if (name.strip().length() > MAX_LENGTH || name.chars().anyMatch(Character::isISOControl)) {
      throw new IllegalArgumentException(
          "Название категории: до " + MAX_LENGTH + " символов без переносов строк");
    }

    this.name = name.strip();
  }

  void setBudget(Money budget) {
    this.budget = budget;
  }

  @Override
  public String toString() {
    return name;
  }
}
