package casa.bras.urlshortner.url.boundary;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.junit.jupiter.api.Assertions.*;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ErrorDTO;
import casa.bras.urlshortner.url.dto.UrlProxyDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UrlProxyResourceITTest extends ProxyIntegrationTest {

  @Test
  void validRequestAndAuth_create_createsNewProxy() {

    String url = "http://localhost";

    createAndAssertProxy(url);
  }

  @Test
  void invalidRequest_create_returnsBadRequest() {

    String url = "localhost";
    var response =
        given()
            .formParam("url", url, ContentType.TEXT)
            .formParam(UrlProxyResource.API_KEY_HEADER, user.getApiKey(), ContentType.TEXT)
            .when()
            .post("/urls")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .extract()
            .as(ErrorDTO.class);

    assertNotNull(response);
    assertEquals(ApplicationError.VALIDATION_ERROR, response.errorType());
  }

  @Test
  void invalidAuth_create_returnsUnauthorized() {

    String url = "http://localhost";

    var response =
        given()
            .formParam("url", url, ContentType.TEXT)
            .formParam(UrlProxyResource.API_KEY_HEADER, UUID.randomUUID(), ContentType.TEXT)
            .when()
            .post("/urls")
            .then()
            .statusCode(UNAUTHORIZED.getStatusCode())
            .extract()
            .as(ErrorDTO.class);

    assertNotNull(response);
    assertEquals(ApplicationError.UNKNOWN_API_KEY, response.errorType());
  }

  @Test
  void validAuth_list_returnsOk() {
    String url = "http://localhost";
    UrlProxyDTO proxy = createAndAssertProxy(url);

    var response =
        given()
            .header(UrlProxyResource.API_KEY_HEADER, user.getApiKey())
            .when()
            .get("/urls")
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
            .header(UrlProxyResource.API_KEY_HEADER, UUID.randomUUID(), ContentType.TEXT)
            .when()
            .get("/urls")
            .then()
            .statusCode(UNAUTHORIZED.getStatusCode())
            .extract()
            .as(ErrorDTO.class);

    assertNotNull(response);
    assertEquals(ApplicationError.UNKNOWN_API_KEY, response.errorType());
  }

  @Test
  void validAuth_delete_returnsNoContentAndDeletesProxy() {
    String url = "http://localhost";
    UrlProxyDTO proxy = createAndAssertProxy(url);

    given()
        .formParam(UrlProxyResource.API_KEY_HEADER, user.getApiKey(), ContentType.TEXT)
        .when()
        .pathParam("hash", proxy.hash())
        .delete("/urls/{hash}")
        .then()
        .statusCode(NO_CONTENT.getStatusCode());

    assertTrue(urlRepository.findByHash(proxy.hash()).isEmpty());
  }

  @Test
  void invalidHash_delete_returnsNoContentAndDoesntDeletesProxy() {
    String url = "http://localhost";
    UrlProxyDTO proxy = createAndAssertProxy(url);

    given()
        .formParam(UrlProxyResource.API_KEY_HEADER, user.getApiKey(), ContentType.TEXT)
        .when()
        .pathParam("hash", "16161")
        .delete("/urls/{hash}")
        .then()
        .statusCode(NO_CONTENT.getStatusCode());

    assertNotNull(urlRepository.findByHash(proxy.hash()));
  }

  @Test
  void invalidAuth_delete_returnsUnauthorized() {

    String url = "http://localhost";
    UrlProxyDTO proxy = createAndAssertProxy(url);

    var response =
        given()
            .formParam(UrlProxyResource.API_KEY_HEADER, UUID.randomUUID(), ContentType.TEXT)
            .when()
            .pathParam("hash", proxy.hash())
            .delete("/urls/{hash}")
            .then()
            .statusCode(UNAUTHORIZED.getStatusCode())
            .extract()
            .as(ErrorDTO.class);

    assertNotNull(response);
    assertEquals(ApplicationError.UNKNOWN_API_KEY, response.errorType());
  }
}
