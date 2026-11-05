package presentation.command;

import java.nio.file.Path;
import presentation.console.Arguments;

public final class ExportCommand extends FinanceCommand {
  public ExportCommand(FinanceContext context) {
    super(context, "export", "<файл.csv>", "Выгрузить кошелёк в CSV");
  }

  @Override
  protected void execute(Arguments args) {
    Path file = Path.of(args.get(0));

    context.wallets().exportTo(wallet(), file);
    terminal().success("Кошелёк выгружен в " + file.toAbsolutePath());
  }
}
