package infrastructure.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import domain.wallet.Money;
import domain.wallet.Operation;
import domain.wallet.OperationType;
import domain.wallet.Wallet;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvWalletExchangeTest {
  private final CsvWalletExchange csv = new CsvWalletExchange();

  @TempDir Path directory;

  @Test
  void writesAndReadsWallet() {
    Wallet wallet = new Wallet("alice");
    wallet.addCategory("Кафе, \"бары\"");
    wallet.setBudget("Кафе, \"бары\"", Money.parse("1500.5"));
    wallet.add(
        OperationType.EXPENSE,
        "Кафе, \"бары\"",
        Money.parse("99.99"),
        LocalDate.of(2026, 11, 5),
        "Ужин, с друзьями");

    Path file = directory.resolve("alice.csv");
    csv.write(wallet, file);
    Wallet restored = csv.read("alice", file);

    Operation operation = restored.operations().get(0);
    assertEquals("Кафе, \"бары\"", operation.category().name());
    assertEquals("Ужин, с друзьями", operation.note());
    assertEquals(Money.parse("1500.5"), operation.category().budget().orElseThrow());
    assertEquals(wallet.balance(), restored.balance());
  }

  @Test
  void reportsBrokenLine() throws Exception {
    Path file = directory.resolve("broken.csv");
    Files.writeString(file, "category,Еда,\noperation,EXPENSE,вчера,Еда,10,\n");

    IllegalArgumentException error =
        assertThrows(IllegalArgumentException.class, () -> csv.read("alice", file));

    assertEquals(true, error.getMessage().contains("строка 2"));
  }

  @Test
  void rejectsZeroBudget() throws Exception {
    Path file = directory.resolve("zero.csv");
    Files.writeString(file, "category,Еда,0\n");

    assertThrows(IllegalArgumentException.class, () -> csv.read("alice", file));
  }
}
