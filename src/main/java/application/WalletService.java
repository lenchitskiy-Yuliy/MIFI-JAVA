package application;

import application.port.WalletExchange;
import application.port.WalletRepository;
import domain.wallet.Money;
import domain.wallet.OperationType;
import domain.wallet.Wallet;
import java.nio.file.Path;
import java.time.LocalDate;

public final class WalletService {
  public static final String TRANSFERS = "Переводы";

  private final WalletRepository wallets;
  private final WalletExchange exchange;
  private final AuthService auth;

  public WalletService(WalletRepository wallets, WalletExchange exchange, AuthService auth) {
    this.wallets = wallets;
    this.exchange = exchange;
    this.auth = auth;
  }

  public Wallet open(String owner) {
    return wallets.load(owner);
  }

  public void save(Wallet wallet) {
    wallets.save(wallet);
  }

  public void transfer(Wallet from, String recipient, Money amount, LocalDate date) {
    if (from.owner().equalsIgnoreCase(recipient)) {
      throw new IllegalArgumentException("Нельзя перевести деньги самому себе");
    }

    if (!auth.exists(recipient)) {
      throw new IllegalArgumentException("Пользователь не найден: " + recipient);
    }

    if (from.balance().compareTo(amount) < 0) {
      throw new IllegalArgumentException("Недостаточно средств, баланс " + from.balance());
    }

    Wallet to = wallets.load(recipient);

    record(from, OperationType.EXPENSE, amount, date, "Перевод для " + recipient);
    record(to, OperationType.INCOME, amount, date, "Перевод от " + from.owner());

    wallets.save(from);
    wallets.save(to);
  }

  public void exportTo(Wallet wallet, Path file) {
    exchange.write(wallet, file);
  }

  public Wallet importFrom(String owner, Path file) {
    return exchange.read(owner, file);
  }

  private static void record(
      Wallet wallet, OperationType type, Money amount, LocalDate date, String note) {
    if (wallet.findCategory(TRANSFERS).isEmpty()) {
      wallet.addCategory(TRANSFERS);
    }

    wallet.add(type, TRANSFERS, amount, date, note);
  }
}
