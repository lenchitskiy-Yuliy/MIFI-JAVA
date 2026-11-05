package presentation.command;

import domain.wallet.Report;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import presentation.console.Arguments;
import presentation.console.Terminal;

public final class SaveReportCommand extends FinanceCommand {
  public SaveReportCommand(FinanceContext context) {
    super(context, "save-report", "<файл> [категория...]", "Сохранить сводку в текстовый файл");
  }

  @Override
  protected void execute(Arguments args) {
    Path file = Path.of(args.get(0));
    Report report = new Report(wallet(), args.from(1), null, null);

    try (PrintWriter writer =
        new PrintWriter(Files.newBufferedWriter(file, StandardCharsets.UTF_8))) {
      new ReportView(new Terminal(writer, false)).print(report);
    } catch (IOException exception) {
      throw new IllegalStateException("Не удалось записать файл " + file);
    }

    terminal().success("Сводка сохранена в " + file.toAbsolutePath());
  }
}
