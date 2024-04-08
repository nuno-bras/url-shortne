package casa.bras.urlshortner.url.entity;

import casa.bras.urlshortner.users.entity.UserEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UrlRepository implements PanacheMongoRepository<UrlEntity> {
  public Optional<URL> findByHash(String hash) {
    return find("hash", hash).firstResultOptional().map(UrlEntity::getUrlAsObject);
  }

  public Optional<UrlEntity> findByUserAndHash(UserEntity user, String hash) {
    return find("userId = ?1 AND hash = ?2", user.id, hash).firstResultOptional();
  }

  public List<UrlEntity> findByUser(UserEntity user) {
    return find("userId = ?1", user.id).list();
  }
}
