package casa.bras.urlshortner.url.entity;

import static org.junit.jupiter.api.Assertions.*;

import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.utilities.IntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UrlRepositoryITTest extends IntegrationTest {
  @Inject UrlRepository repository;

  private final Date now = Date.from(Instant.now());
  private final UserEntity user = new UserEntity(new ObjectId(now), "user@test.com", "username");
  private UrlEntity entity;

  @BeforeEach
  void beforeEach() throws MalformedURLException {
    repository.deleteAll();
    entity = new UrlEntity(new URL("http://localhost:8080"), user);
    repository.persist(entity);
  }

  @Test
  void validHash_findByHash_returnsOptionalWithEntity() {
    Optional<URL> result = repository.findByHash(entity.getHash());
    assertNotNull(result);
    assertTrue(result.isPresent());

    URL url = result.get();
    assertEquals(entity.getUrlAsObject(), url);
  }

  @Test
  void invalidUrl_findByHash_returnsEmptyOptional() {
    Optional<URL> result = repository.findByHash(entity.getHash() + "1");
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void validUserAndHash_findByUserAndHash_returnsOptionalWithEntity() {
    Optional<UrlEntity> result = repository.findByUserAndHash(user, entity.getHash());
    assertNotNull(result);
    assertTrue(result.isPresent());

    UrlEntity entityFromOptional = result.get();
    assertEquals(entity.id, entityFromOptional.id);
  }

  @Test
  void invalidUserOrHash_findByUserAndHash_returnsOptionalEmpty() {
    Optional<UrlEntity> wrongHash = repository.findByUserAndHash(user, entity.getHash() + "1");
    assertNotNull(wrongHash);
    assertTrue(wrongHash.isEmpty());

    UserEntity anotherUser =
        new UserEntity(
            new ObjectId(Date.from(Instant.now().plusSeconds(1))), "test@test.com", "name");
    Optional<UrlEntity> wrongUser = repository.findByUserAndHash(anotherUser, entity.getHash());
    assertNotNull(wrongUser);
    assertTrue(wrongUser.isEmpty());
  }

  @Test
  void validUser_findByUser_returnsListOfAllEntitiesRelatedToUser() throws MalformedURLException {

    UserEntity anotherUser =
        new UserEntity(
            new ObjectId(Date.from(Instant.now().plusSeconds(1))), "test@test.com", "name");
    repository.persist(new UrlEntity(new URL("http://localhost"), anotherUser));

    List<UrlEntity> result = repository.findByUser(user);
    assertNotNull(result);
    assertEquals(1, result.size());

    assertEquals(entity.id, result.get(0).id);
  }
}
