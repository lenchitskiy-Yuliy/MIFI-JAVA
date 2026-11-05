package application;

import application.port.UserStore;
import java.util.Optional;
import java.util.UUID;

public final class Session {
  private final UserStore store;
  private volatile UUID user;

  public Session(UserStore store) {
    this.store = store;
    this.user = store.load().orElse(null);
  }

  public Optional<UUID> user() {
    return Optional.ofNullable(user);
  }

  public UUID require() {
    if (user == null) {
      throw new IllegalStateException("У вас пока нет ссылок. Создайте первую: create <адрес>");
    }

    return user;
  }

  public void use(UUID user) {
    this.user = user;
    store.save(user);
  }

  public void forget() {
    user = null;
    store.clear();
  }
}
