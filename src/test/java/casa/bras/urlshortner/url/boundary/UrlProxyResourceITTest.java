package casa.bras.urlshortner.url.boundary;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.junit.jupiter.api.Assertions.*;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ErrorDTO;
import casa.bras.urlshortner.url.dto.CreateUrlDTO;
import casa.bras.urlshortner.url.dto.UrlProxyDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UrlProxyResourceITTest extends ProxyIntegrationTest {

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
            .post("/urls")
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
    UrlProxyDTO proxy = createAndAssertProxy(new CreateUrlDTO("http://localhost"));

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
            .header(UrlProxyResource.API_KEY_HEADER, UUID.randomUUID())
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
    UrlProxyDTO proxy = createAndAssertProxy(new CreateUrlDTO("http://localhost"));

    given()
        .header(UrlProxyResource.API_KEY_HEADER, user.getApiKey())
        .when()
        .pathParam("hash", proxy.hash())
        .delete("/urls/{hash}")
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
        .pathParam("hash", "16161")
        .delete("/urls/{hash}")
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
