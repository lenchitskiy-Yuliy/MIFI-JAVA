package presentation.command;

import domain.wallet.Report;
import presentation.console.Arguments;

public final class ReportCommand extends FinanceCommand {
  public ReportCommand(FinanceContext context) {
    super(context, "report", "[категория...]", "Сводка по всем или выбранным категориям");
  }

  @Override
  protected void execute(Arguments args) {
    new ReportView(terminal()).print(new Report(wallet(), args.from(0), null, null));
  }
}
