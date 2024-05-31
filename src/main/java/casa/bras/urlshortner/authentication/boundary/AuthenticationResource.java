package casa.bras.urlshortner.authentication.boundary;

import casa.bras.urlshortner.authentication.control.AuthenticationUseCase;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/auth")
public class AuthenticationResource {

  private final AuthenticationUseCase authenticationUseCase;

  public AuthenticationResource(AuthenticationUseCase authenticationUseCase) {
    this.authenticationUseCase = authenticationUseCase;
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Authenticates")
  public Response authenticate(
      @FormParam("email") String email, @FormParam("password") String password) {
    return Response.temporaryRedirect(
            URI.create("/ui/" + authenticationUseCase.execute(email, password).id()))
        .build();
  }
}
