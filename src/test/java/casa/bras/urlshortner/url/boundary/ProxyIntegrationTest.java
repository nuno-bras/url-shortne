package casa.bras.urlshortner.url.boundary;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import casa.bras.urlshortner.url.dto.UrlProxyDTO;
import casa.bras.urlshortner.url.entity.UrlRepository;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import casa.bras.utilities.IntegrationTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;

public class ProxyIntegrationTest extends IntegrationTest {

  @Inject UrlRepository urlRepository;
  @Inject UserRepository userRepository;

  protected UserEntity user;

  @BeforeEach
  void beforeEach() {

    userRepository.deleteAll();
    urlRepository.deleteAll();

    user = new UserEntity("test@test.test", "name");
    userRepository.persist(user);
  }

  protected UrlProxyDTO createAndAssertProxy(String url) {
    var response =
        given()
            .formParam("url", url, ContentType.TEXT)
            .formParam(UrlProxyResource.API_KEY_HEADER, user.getApiKey(), ContentType.TEXT)
            .when()
            .post("/urls")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(UrlProxyDTO.class);

    assertNotNull(response);
    assertEquals(url, response.url());
    assertNotNull(response.hash());

    assertNotNull(urlRepository.findByHash(response.hash()));

    return response;
  }
}
