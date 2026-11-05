package domain.wallet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Objects;
import java.util.regex.Pattern;

public record Money(BigDecimal amount) implements Comparable<Money> {
  public static final Money ZERO = new Money(BigDecimal.ZERO);

  private static final Pattern FORMAT = Pattern.compile("\\d{1,12}([.,]\\d{1,2})?");

  public Money {
    amount = Objects.requireNonNull(amount).setScale(2, RoundingMode.HALF_UP);
  }

  public static Money parse(String value) {
    if (!FORMAT.matcher(value).matches()) {
      throw new IllegalArgumentException(
          "Сумма должна быть положительным числом, не больше двух знаков после запятой: " + value);
    }

    Money money = new Money(new BigDecimal(value.replace(',', '.')));

    if (money.isZero()) {
      throw new IllegalArgumentException("Сумма должна быть больше нуля");
    }

    return money;
  }

  public Money plus(Money other) {
    return new Money(amount.add(other.amount));
  }

  public Money minus(Money other) {
    return new Money(amount.subtract(other.amount));
  }

  public int percentOf(Money total) {
    return amount
        .multiply(BigDecimal.valueOf(100))
        .divide(total.amount, RoundingMode.DOWN)
        .intValue();
  }

  public boolean isNegative() {
    return amount.signum() < 0;
  }

  public boolean isZero() {
    return amount.signum() == 0;
  }

  @Override
  public int compareTo(Money other) {
    return amount.compareTo(other.amount);
  }

  @Override
  public String toString() {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setGroupingSeparator(' ');
    symbols.setDecimalSeparator('.');
    symbols.setMinusSign('-');
    return new DecimalFormat("#,##0.00", symbols).format(amount);
  }

  public String plain() {
    return amount.toPlainString();
  }
}
