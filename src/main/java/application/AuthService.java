package application;

import application.port.PasswordHasher;
import application.port.UserRepository;
import domain.user.User;

public final class AuthService {
  private static final int MIN_PASSWORD_LENGTH = 4;

  private final UserRepository users;
  private final PasswordHasher hasher;

  public AuthService(UserRepository users, PasswordHasher hasher) {
    this.users = users;
    this.hasher = hasher;
  }

  public User register(String login, String password) {
    if (users.find(login).isPresent()) {
      throw new IllegalArgumentException("Логин уже занят: " + login);
    }

    if (password.length() < MIN_PASSWORD_LENGTH) {
      throw new IllegalArgumentException(
          "Пароль должен быть не короче " + MIN_PASSWORD_LENGTH + " символов");
    }

    User user = new User(login, hasher.hash(password));
    users.save(user);
    return user;
  }

  public User login(String login, String password) {
    return users
        .find(login)
        .filter(user -> hasher.matches(password, user.passwordHash()))
        .orElseThrow(() -> new IllegalArgumentException("Неверный логин или пароль"));
  }

  public boolean exists(String login) {
    return users.find(login).isPresent();
  }
}
