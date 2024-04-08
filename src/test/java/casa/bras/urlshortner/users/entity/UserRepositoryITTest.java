package casa.bras.urlshortner.users.entity;

import static org.junit.jupiter.api.Assertions.*;

import casa.bras.utilities.IntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Tag("integration")
class UserRepositoryITTest extends IntegrationTest {
  @Inject UserRepository repository;

  private UserEntity entity;

  @BeforeEach
  void beforeEach() {
    repository.deleteAll();

    entity = new UserEntity("john@doe.com", "John Doe");
    repository.save(entity);
  }

  @Test
  void validEmail_emailExists_returnsTrue() {
    assertTrue(repository.emailExists(entity.getEmail()));
  }

  @Test
  void invalidEmail_emailExists_returnsFalse() {
    assertFalse(repository.emailExists("none@doe.com"));
  }

  @Test
  void validApiKey_findByApiKey_returnsOptionalWithUserEntity() {
    Optional<UserEntity> result = repository.findByApiKey(entity.getApiKey());
    assertNotNull(result);
    assertTrue(result.isPresent());

    UserEntity user = result.get();
    assertEquals(entity.getApiKey(), user.getApiKey());
    assertEquals(entity.getName(), user.getName());
    assertEquals(entity.getEmail(), user.getEmail());
  }

  @Test
  void invalidApiKey_findByApiKey_returnsEmptyOptional() {
    Optional<UserEntity> result = repository.findByApiKey(UUID.randomUUID());
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void newUser_save_persistsUser() {
    var user = new UserEntity("doe@doe.com", "Doe Doe");
    repository.save(user);
    assertNotNull(user.id);
  }
}
