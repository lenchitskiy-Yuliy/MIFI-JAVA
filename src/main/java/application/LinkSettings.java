package application;

import java.time.Duration;

public record LinkSettings(Duration ttl, int defaultLimit, int maxLimit) {
  public LinkSettings {
    if (ttl.isNegative() || ttl.isZero()) {
      throw new IllegalArgumentException("Время жизни ссылки должно быть больше нуля");
    }

    if (defaultLimit < 1 || defaultLimit > maxLimit) {
      throw new IllegalArgumentException("Лимит по умолчанию должен быть от 1 до " + maxLimit);
    }
  }
}
