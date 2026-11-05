package presentation.command;

import domain.wallet.Wallet;
import java.nio.file.Path;
import presentation.console.Arguments;

public final class ImportCommand extends FinanceCommand {
  public ImportCommand(FinanceContext context) {
    super(context, "import", "<файл.csv>", "Заменить кошелёк данными из CSV");
  }

  @Override
  protected void execute(Arguments args) {
    Wallet wallet = context.wallets().importFrom(wallet().owner(), Path.of(args.get(0)));

    context.session().replace(wallet);
    terminal()
        .success(
            "Загружено категорий: "
                + wallet.categories().size()
                + ", операций: "
                + wallet.operations().size());
    warn(context.alerts().check(wallet));
  }
}
