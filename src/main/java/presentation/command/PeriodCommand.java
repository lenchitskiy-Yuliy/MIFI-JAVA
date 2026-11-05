package presentation.command;

import domain.wallet.Report;
import presentation.console.Arguments;

public final class PeriodCommand extends FinanceCommand {
  public PeriodCommand(FinanceContext context) {
    super(context, "period", "<с> <по> [категория...]", "Сводка за период, даты ГГГГ-ММ-ДД");
  }

  @Override
  protected void execute(Arguments args) {
    Report report =
        new Report(
            wallet(),
            args.from(2),
            args.get(0, Arguments::parseDate),
            args.get(1, Arguments::parseDate));

    new ReportView(terminal()).print(report);
  }
}
