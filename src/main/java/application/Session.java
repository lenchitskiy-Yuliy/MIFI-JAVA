package application;

import domain.user.User;
import domain.wallet.Wallet;

public final class Session {
  private User user;
  private Wallet wallet;

  public void start(User user, Wallet wallet) {
    if (isActive()) {
      throw new IllegalStateException("Вы уже вошли как " + this.user.login() + ". Сначала logout");
    }

    this.user = user;
    this.wallet = wallet;
  }

  public void replace(Wallet wallet) {
    this.wallet = wallet;
  }

  public void end() {
    user = null;
    wallet = null;
  }

  public boolean isActive() {
    return user != null;
  }

  public User user() {
    requireActive();
    return user;
  }

  public Wallet wallet() {
    requireActive();
    return wallet;
  }

  private void requireActive() {
    if (!isActive()) {
      throw new IllegalStateException("Сначала войдите: login <логин> <пароль>");
    }
  }
}
