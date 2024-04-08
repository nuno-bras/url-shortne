package casa.bras.urlshortner.users.boundary;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.junit.jupiter.api.Assertions.*;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ErrorDTO;
import casa.bras.urlshortner.users.dto.CreateUserDTO;
import casa.bras.urlshortner.users.dto.UserDTO;
import casa.bras.urlshortner.users.entity.UserRepository;
import casa.bras.utilities.IntegrationTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Tag("integration")
@TestHTTPEndpoint(UserResource.class)
class UserResourceITTest extends IntegrationTest {
  @Inject UserRepository repository;

  @BeforeEach
  void beforeEach() {
    repository.deleteAll();
  }

  @Test
  void validRequest_create_createsNewUser() {
    var request = new CreateUserDTO("test@test.com", "name");
    createAndAssertUser(request);
  }

  @Test
  void invalidRequest_create_returnsBadRequest() {
    var request = new CreateUserDTO("testest", "name");

    var response =
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post()
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .extract()
            .as(ErrorDTO.class);

    assertNotNull(response);
    assertEquals(ApplicationError.VALIDATION_ERROR, response.errorType());
  }

  @Test
  void validId_get_returnsUser() {
    UserDTO user = createAndAssertUser(new CreateUserDTO("test@test.com", "name"));

    var response =
        given()
            .when()
            .get(user.id())
            .then()
            .statusCode(OK.getStatusCode())
            .extract()
            .as(UserDTO.class);

    assertNotNull(response);
    assertEquals(user.id(), response.id());
    assertEquals(user.name(), response.name());
    assertEquals(user.email(), response.email());
    assertEquals(user.apiKey(), response.apiKey());
  }

  @Test
  void invalidId_get_returnsNotFound() {
    var response =
        given()
            .when()
            .get("6e491ca3fe4aabf7a60e0b07")
            .then()
            .statusCode(NOT_FOUND.getStatusCode())
            .extract()
            .as(ErrorDTO.class);

    assertNotNull(response);
    assertEquals(ApplicationError.USER_NOT_FOUND.ordinal(), response.errorCode());
    assertEquals(ApplicationError.USER_NOT_FOUND, response.errorType());
    assertEquals(ApplicationError.USER_NOT_FOUND.getMessage(), response.errorMessage());
  }

  @Test
  void validId_delete_returnsNoContentAndDeletesUser() {
    UserDTO user = createAndAssertUser(new CreateUserDTO("test@test.com", "name"));

    given().when().delete(user.id()).then().statusCode(NO_CONTENT.getStatusCode());

    assertNull(repository.findById(new ObjectId(user.id())));
  }

  @Test
  void invalidId_delete_returnsNoContentAndDoesntDeleteUser() {
    UserDTO user = createAndAssertUser(new CreateUserDTO("test@test.com", "name"));

    given().when().delete("6e491ca3fe4aabf7a60e0b07").then().statusCode(NO_CONTENT.getStatusCode());

    assertNotNull(repository.findById(new ObjectId(user.id())));
  }

  private UserDTO createAndAssertUser(CreateUserDTO request) {

    var response =
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post()
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(UserDTO.class);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.email());
    assertEquals(request.getName(), response.name());
    assertNotNull(response.apiKey());

    assertTrue(repository.findByApiKey(UUID.fromString(response.apiKey())).isPresent());

    return response;
  }
}
