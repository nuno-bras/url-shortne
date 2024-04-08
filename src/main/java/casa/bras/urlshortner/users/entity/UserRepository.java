package casa.bras.urlshortner.users.entity;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<UserEntity> {

  public boolean emailExists(String email) {
    return count("email", email) > 0;
  }

  public Optional<UserEntity> findByApiKey(UUID apiKey) {
    return find("apiKey", apiKey).firstResultOptional();
  }

  public UserEntity save(UserEntity user) {
    persist(user);
    return user;
  }
}
