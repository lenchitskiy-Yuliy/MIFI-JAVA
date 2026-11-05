package application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import domain.wallet.Money;
import domain.wallet.OperationType;
import domain.wallet.Wallet;
import infrastructure.security.Pbkdf2PasswordHasher;
import infrastructure.storage.CsvWalletExchange;
import infrastructure.storage.FileUserRepository;
import infrastructure.storage.FileWalletRepository;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ServicesTest {
  @TempDir Path data;

  private AuthService auth;
  private WalletService wallets;

  @BeforeEach
  void setUp() {
    CsvWalletExchange csv = new CsvWalletExchange();
    auth =
        new AuthService(
            new FileUserRepository(data.resolve("users.csv")), new Pbkdf2PasswordHasher());
    wallets = new WalletService(new FileWalletRepository(data.resolve("wallets"), csv), csv, auth);
    auth.register("alice", "secret");
    auth.register("bob", "secret");
  }

  @Test
  void checksPassword() {
    assertEquals("alice", auth.login("alice", "secret").login());
    assertThrows(IllegalArgumentException.class, () -> auth.login("alice", "wrong"));
    assertThrows(IllegalArgumentException.class, () -> auth.register("Alice", "secret"));
  }

  @Test
  void usersSurviveRestart() {
    AuthService restarted =
        new AuthService(
            new FileUserRepository(data.resolve("users.csv")), new Pbkdf2PasswordHasher());

    assertEquals("bob", restarted.login("bob", "secret").login());
  }

  @Test
  void transferRecordsExpenseAndIncome() {
    Wallet alice = wallets.open("alice");
    alice.addCategory("Зарплата");
    alice.add(OperationType.INCOME, "Зарплата", Money.parse("1000"), LocalDate.now(), "");

    wallets.transfer(alice, "bob", Money.parse("400"), LocalDate.now());

    assertEquals(Money.parse("600"), alice.balance());
    assertEquals(Money.parse("400"), wallets.open("bob").balance());
    assertEquals(Money.parse("600"), wallets.open("alice").balance());
  }

  @Test
  void rejectsInvalidTransfer() {
    Wallet alice = wallets.open("alice");
    Money amount = Money.parse("100");

    assertThrows(
        IllegalArgumentException.class,
        () -> wallets.transfer(alice, "bob", amount, LocalDate.now()));
    assertThrows(
        IllegalArgumentException.class,
        () -> wallets.transfer(alice, "carl", amount, LocalDate.now()));
    assertThrows(
        IllegalArgumentException.class,
        () -> wallets.transfer(alice, "alice", amount, LocalDate.now()));
  }
}
