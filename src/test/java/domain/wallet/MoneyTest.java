package domain.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MoneyTest {
  @Test
  void parsesCommaAndFormatsWithGrouping() {
    assertEquals("63 000.50", Money.parse("63000,5").toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "abc", "-10", "0", "1.234", "1e5"})
  void rejectsInvalidAmount(String value) {
    assertThrows(IllegalArgumentException.class, () -> Money.parse(value));
  }

  @Test
  void calculatesPercent() {
    assertEquals(80, Money.parse("800").percentOf(Money.parse("1000")));
  }
}
