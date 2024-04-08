package casa.bras.urlshortner.common.healthcheck;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.junit.jupiter.api.Assertions.*;

import casa.bras.utilities.IntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Tag("integration")
class HealthCheckResourceITTest extends IntegrationTest {

  @Test
  void healthCheck() {
    String body =
        given()
            .accept(ContentType.TEXT)
            .when()
            .get("/health")
            .then()
            .statusCode(OK.getStatusCode())
            .extract()
            .body()
            .asString();

    assertEquals("OK", body);
  }
}
