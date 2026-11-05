package domain.wallet;

public enum OperationType {
  INCOME("Доходы"),
  EXPENSE("Расходы");

  private final String title;

  OperationType(String title) {
    this.title = title;
  }

  public String title() {
    return title;
  }
}
