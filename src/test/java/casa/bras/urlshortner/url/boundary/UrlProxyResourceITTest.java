package casa.bras.urlshortner.url.boundary;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.junit.jupiter.api.Assertions.*;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ErrorDTO;
import casa.bras.urlshortner.url.dto.CreateUrlDTO;
import casa.bras.urlshortner.url.dto.UrlProxyDTO;
import casa.bras.urlshortner.url.entity.UrlRepository;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import casa.bras.utilities.IntegrationTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(UrlProxyResource.class)
class UrlProxyResourceITTest extends IntegrationTest {
  @Inject UrlRepository urlRepository;
  @Inject UserRepository userRepository;

  private UserEntity user;

  @BeforeEach
  void beforeEach() {

    userRepository.deleteAll();
    urlRepository.deleteAll();

    user = new UserEntity("test@test.test", "name");
    userRepository.persist(user);
  }

  @Test
  void validRequestAndAuth_create_createsNewProxy() {

    var request = new CreateUrlDTO("http://localhost");

    createAndAssertProxy(request);
  }

  @Test
  void invalidRequest_create_returnsBadRequest() {

    var request = new CreateUrlDTO("localhost");

    var response =
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .header(UrlProxyResource.API_KEY_HEADER, user.getApiKey())
            .when()
            .post()
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .extract()
            .as(ErrorDTO.class);

    assertNotNull(response);
    assertEquals(ApplicationError.URL_FORMAT_INVALID, response.errorType());
  }

  @Test
  void invalidAuth_create_returnsUnauthorized() {

    var request = new CreateUrlDTO("http://localhost");

    var response =
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .header(UrlProxyResource.API_KEY_HEADER, UUID.randomUUID())
            .when()
            .post()
            .then()
            .statusCode(UNAUTHORIZED.getStatusCode())
            .extract()
            .as(ErrorDTO.class);

    assertNotNull(response);
    assertEquals(ApplicationError.UNKNOWN_API_KEY, response.errorType());
  }

  @Test
  void validAuth_list_returnsOk() {
    UrlProxyDTO proxy = createAndAssertProxy(new CreateUrlDTO("http://localhost"));

    var response =
        given()
            .header(UrlProxyResource.API_KEY_HEADER, user.getApiKey())
            .when()
            .get()
            .then()
            .statusCode(OK.getStatusCode())
            .extract()
            .as(UrlProxyDTO[].class);

    assertNotNull(response);
    assertEquals(1, response.length);
    assertEquals(proxy, response[0]);
  }

  @Test
  void invalidAuth_list_returnsUnauthorized() {

    var response =
        given()
            .header(UrlProxyResource.API_KEY_HEADER, UUID.randomUUID())
            .when()
            .get()
            .then()
            .statusCode(UNAUTHORIZED.getStatusCode())
            .extract()
            .as(ErrorDTO.class);

    assertNotNull(response);
    assertEquals(ApplicationError.UNKNOWN_API_KEY, response.errorType());
  }

  @Test
  void validAuth_delete_returnsNoContentAndDeletesProxy() {
    UrlProxyDTO proxy = createAndAssertProxy(new CreateUrlDTO("http://localhost"));

    given()
        .header(UrlProxyResource.API_KEY_HEADER, user.getApiKey())
        .when()
        .delete(proxy.hash())
        .then()
        .statusCode(NO_CONTENT.getStatusCode());

    assertTrue(urlRepository.findByHash(proxy.hash()).isEmpty());
  }

  @Test
  void invalidHash_delete_returnsNoContentAndDoesntDeletesProxy() {

    UrlProxyDTO proxy = createAndAssertProxy(new CreateUrlDTO("http://localhost"));

    given()
        .header(UrlProxyResource.API_KEY_HEADER, user.getApiKey())
        .when()
        .delete("16161")
        .then()
        .statusCode(NO_CONTENT.getStatusCode());

    assertNotNull(urlRepository.findByHash(proxy.hash()));
  }

  @Test
  void invalidAuth_delete_returnsUnauthorized() {

    UrlProxyDTO proxy = createAndAssertProxy(new CreateUrlDTO("http://localhost"));

    var response =
        given()
            .header(UrlProxyResource.API_KEY_HEADER, UUID.randomUUID())
            .when()
            .delete(proxy.hash())
            .then()
            .statusCode(UNAUTHORIZED.getStatusCode())
            .extract()
            .as(ErrorDTO.class);

    assertNotNull(response);
    assertEquals(ApplicationError.UNKNOWN_API_KEY, response.errorType());
  }

  private UrlProxyDTO createAndAssertProxy(CreateUrlDTO request) {
    var response =
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .header(UrlProxyResource.API_KEY_HEADER, user.getApiKey())
            .when()
            .post()
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(UrlProxyDTO.class);

    assertNotNull(response);
    assertEquals(request.url(), response.url());
    assertNotNull(response.hash());

    assertNotNull(urlRepository.findByHash(response.hash()));

    return response;
  }
}
