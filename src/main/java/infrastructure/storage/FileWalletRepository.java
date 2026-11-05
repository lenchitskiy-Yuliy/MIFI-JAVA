package infrastructure.storage;

import application.port.WalletExchange;
import application.port.WalletRepository;
import domain.wallet.Wallet;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class FileWalletRepository implements WalletRepository {
  private final Path directory;
  private final WalletExchange format;

  public FileWalletRepository(Path directory, WalletExchange format) {
    this.directory = directory;
    this.format = format;
  }

  @Override
  public Wallet load(String owner) {
    Path file = file(owner);
    return Files.exists(file) ? format.read(owner, file) : new Wallet(owner);
  }

  @Override
  public void save(Wallet wallet) {
    format.write(wallet, file(wallet.owner()));
  }

  private Path file(String owner) {
    return directory.resolve(owner.toLowerCase(Locale.ROOT) + ".csv");
  }
}
