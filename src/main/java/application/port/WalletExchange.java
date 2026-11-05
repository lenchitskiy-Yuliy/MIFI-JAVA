package application.port;

import domain.wallet.Wallet;
import java.nio.file.Path;

public interface WalletExchange {
  void write(Wallet wallet, Path file);

  Wallet read(String owner, Path file);
}
