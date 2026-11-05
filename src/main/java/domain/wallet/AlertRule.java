package domain.wallet;

import java.util.List;

public interface AlertRule {
  List<Alert> check(Wallet wallet);
}
