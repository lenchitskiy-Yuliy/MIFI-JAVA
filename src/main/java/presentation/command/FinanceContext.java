package presentation.command;

import application.AuthService;
import application.Session;
import application.WalletService;
import domain.wallet.Alerts;
import presentation.console.Terminal;

public record FinanceContext(
    Session session, AuthService auth, WalletService wallets, Alerts alerts, Terminal terminal) {}
