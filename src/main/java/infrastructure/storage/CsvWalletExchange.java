package infrastructure.storage;

import application.port.WalletExchange;
import domain.wallet.Category;
import domain.wallet.Money;
import domain.wallet.Operation;
import domain.wallet.OperationType;
import domain.wallet.Wallet;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class CsvWalletExchange implements WalletExchange {
  @Override
  public void write(Wallet wallet, Path file) {
    List<String> lines = new ArrayList<>();

    for (Category category : wallet.categories()) {
      lines.add(
          Csv.line("category", category.name(), category.budget().map(Money::plain).orElse("")));
    }

    for (Operation operation : wallet.operations()) {
      lines.add(
          Csv.line(
              "operation",
              operation.type(),
              operation.date(),
              operation.category().name(),
              operation.amount().plain(),
              operation.note()));
    }

    Csv.write(file, lines);
  }

  @Override
  public Wallet read(String owner, Path file) {
    List<String> lines;

    try {
      lines = Files.readAllLines(file, StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new IllegalArgumentException("Не удалось прочитать файл " + file);
    }

    Wallet wallet = new Wallet(owner);

    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).isBlank()) {
        continue;
      }

      try {
        apply(wallet, Csv.parse(lines.get(i)));
      } catch (RuntimeException exception) {
        throw new IllegalArgumentException(
            "Ошибка в файле " + file + ", строка " + (i + 1) + ": " + exception.getMessage());
      }
    }

    return wallet;
  }

  private static void apply(Wallet wallet, List<String> row) {
    switch (row.get(0)) {
      case "category" -> {
        wallet.addCategory(row.get(1));

        if (!row.get(2).isEmpty()) {
          wallet.setBudget(row.get(1), money(row.get(2)));
        }
      }
      case "operation" ->
          wallet.add(
              OperationType.valueOf(row.get(1)),
              row.get(3),
              money(row.get(4)),
              LocalDate.parse(row.get(2)),
              row.get(5));
      default -> throw new IllegalArgumentException("неизвестный тип записи " + row.get(0));
    }
  }

  private static Money money(String value) {
    return Money.parse(value);
  }
}
