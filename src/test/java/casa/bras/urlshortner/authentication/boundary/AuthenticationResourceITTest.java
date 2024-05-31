package casa.bras.urlshortner.authentication.boundary;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import casa.bras.urlshortner.users.dto.CreateUserDTO;
import casa.bras.urlshortner.users.dto.UserDTO;
import casa.bras.urlshortner.users.entity.UserRepository;
import casa.bras.utilities.IntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Tag("integration")
class AuthenticationResourceITTest extends IntegrationTest {

  @Inject UserRepository repository;

  @BeforeEach
  void beforeEach() {
    repository.deleteAll();
  }

  @Test
  void correctEmailAndPassword_authenticate_returnsRedirectToUserLandingPage() {

    var createUserRequest = new CreateUserDTO("test@test.com", "password");
    UserDTO createdUser = createUser(createUserRequest);

    String userResource =
        given()
            .formParam("email", "test@test.com")
            .formParam("password", "password")
            .when()
            .post("/auth")
            .then()
            .statusCode(TEMPORARY_REDIRECT.getStatusCode())
            .extract()
            .header(HttpHeaders.LOCATION);

    String expected = String.format("/ui/%s", createdUser.id());
    assertTrue(userResource.endsWith(expected));
  }

  @Test
  void incorrectEmailAndPassword_authenticate_returnsUserDTO() {

    var createUserRequest = new CreateUserDTO("test@test.com", "password");
    createUser(createUserRequest);

    given()
        .formParam("email", "fake@test.com")
        .formParam("password", "password")
        .when()
        .post("/auth")
        .then()
        .statusCode(UNAUTHORIZED.getStatusCode());
  }

  private UserDTO createUser(CreateUserDTO request) {

    return given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/users")
        .then()
        .statusCode(CREATED.getStatusCode())
        .extract()
        .as(UserDTO.class);
  }
}
