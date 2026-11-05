package presentation.command;

import domain.wallet.Alert;
import domain.wallet.Wallet;
import java.util.List;
import presentation.console.Command;
import presentation.console.Terminal;

public abstract class FinanceCommand extends Command {
  protected final FinanceContext context;

  protected FinanceCommand(FinanceContext context, String name, String params, String description) {
    super(name, params, description);
    this.context = context;
  }

  protected Wallet wallet() {
    return context.session().wallet();
  }

  protected Terminal terminal() {
    return context.terminal();
  }

  protected void warn(List<Alert> alerts) {
    alerts.forEach(alert -> terminal().warning(alert.message()));
  }
}
