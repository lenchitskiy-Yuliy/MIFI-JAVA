package domain.user;

public record User(String login, String passwordHash) {
  public User {
    if (login == null || !login.matches("[a-zA-Z0-9_]{3,20}")) {
      throw new IllegalArgumentException("Логин: от 3 до 20 латинских букв, цифр или _");
    }
  }
}
