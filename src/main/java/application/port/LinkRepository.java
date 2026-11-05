package application.port;

import domain.link.ShortLink;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinkRepository {
  Optional<ShortLink> find(String code);

  List<ShortLink> findByOwner(UUID owner);

  List<ShortLink> findAll();

  void save(ShortLink link);

  void delete(String code);
}
