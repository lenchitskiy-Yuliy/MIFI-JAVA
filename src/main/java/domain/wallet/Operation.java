package domain.wallet;

import java.time.LocalDate;

public record Operation(
    OperationType type, Category category, Money amount, LocalDate date, String note) {
  public boolean is(OperationType type) {
    return this.type == type;
  }

  public boolean isIn(LocalDate from, LocalDate to) {
    return (from == null || !date.isBefore(from)) && (to == null || !date.isAfter(to));
  }
}
