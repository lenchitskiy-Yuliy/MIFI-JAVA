package application.port;

import domain.wallet.Wallet;

public interface WalletRepository {
  Wallet load(String owner);

  void save(Wallet wallet);
}
