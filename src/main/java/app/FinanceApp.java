package app;

import application.AuthService;
import application.Session;
import application.WalletService;
import domain.wallet.Alerts;
import domain.wallet.OperationType;
import infrastructure.security.Pbkdf2PasswordHasher;
import infrastructure.storage.CsvWalletExchange;
import infrastructure.storage.FileUserRepository;
import infrastructure.storage.FileWalletRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import presentation.command.AddCategoryCommand;
import presentation.command.BudgetCommand;
import presentation.command.CategoriesCommand;
import presentation.command.ExportCommand;
import presentation.command.FinanceContext;
import presentation.command.HistoryCommand;
import presentation.command.ImportCommand;
import presentation.command.LoginCommand;
import presentation.command.LogoutCommand;
import presentation.command.OperationCommand;
import presentation.command.PeriodCommand;
import presentation.command.RegisterCommand;
import presentation.command.RemoveBudgetCommand;
import presentation.command.RemoveCategoryCommand;
import presentation.command.RenameCategoryCommand;
import presentation.command.ReportCommand;
import presentation.command.SaveReportCommand;
import presentation.command.TransferCommand;
import presentation.console.Console;
import presentation.console.Terminal;

public final class FinanceApp {
  private final Terminal terminal;
  private final Session session = new Session();
  private final WalletService wallets;
  private final Console console;

  public FinanceApp(AppSettings settings, Terminal terminal) {
    this.terminal = terminal;

    Path data = settings.dataDirectory();
    CsvWalletExchange csv = new CsvWalletExchange();
    AuthService auth =
        new AuthService(
            new FileUserRepository(data.resolve("users.csv")), new Pbkdf2PasswordHasher());
    wallets = new WalletService(new FileWalletRepository(data.resolve("wallets"), csv), csv, auth);

    FinanceContext context =
        new FinanceContext(session, auth, wallets, Alerts.standard(), terminal);

    console =
        new Console(
            terminal,
            this::prompt,
            List.of(
                new RegisterCommand(context),
                new LoginCommand(context),
                new LogoutCommand(context),
                new OperationCommand(context, OperationType.INCOME),
                new OperationCommand(context, OperationType.EXPENSE),
                new TransferCommand(context),
                new AddCategoryCommand(context),
                new RenameCategoryCommand(context),
                new RemoveCategoryCommand(context),
                new CategoriesCommand(context),
                new BudgetCommand(context),
                new RemoveBudgetCommand(context),
                new ReportCommand(context),
                new PeriodCommand(context),
                new HistoryCommand(context),
                new SaveReportCommand(context),
                new ExportCommand(context),
                new ImportCommand(context)));
  }

  public void run(BufferedReader input) throws IOException {
    terminal.title("Личные финансы");
    terminal.hint("Введите help, чтобы увидеть команды, exit для выхода");

    try {
      console.run(input);
    } finally {
      save();
    }
  }

  public synchronized void shutdown() {
    if (session.isActive()) {
      terminal.println();
      save();
    }
  }

  private synchronized void save() {
    if (!session.isActive()) {
      return;
    }

    wallets.save(session.wallet());
    terminal.success("Кошелёк " + session.user().login() + " сохранён");
    session.end();
  }

  private String prompt() {
    return session.isActive() ? session.user().login() : "finance";
  }
}
