package application.port;

import java.util.Optional;
import java.util.UUID;

public interface UserStore {
  Optional<UUID> load();

  void save(UUID user);

  void clear();
}
