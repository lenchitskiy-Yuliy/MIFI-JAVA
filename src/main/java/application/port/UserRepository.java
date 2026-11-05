package application.port;

import domain.user.User;
import java.util.Optional;

public interface UserRepository {
  Optional<User> find(String login);

  void save(User user);
}
