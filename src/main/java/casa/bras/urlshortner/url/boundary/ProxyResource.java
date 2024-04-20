package casa.bras.urlshortner.url.boundary;

import casa.bras.urlshortner.url.control.RedirectUseCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/")
@Tag(name = "Redirect")
public class ProxyResource {
  private final RedirectUseCase redirectUseCase;

  public ProxyResource(RedirectUseCase redirectUseCase) {
    this.redirectUseCase = redirectUseCase;
  }

  @GET
  @Path("/{hash:\\w{7}}")
  @Operation(summary = "Redirection")
  @APIResponse(
      responseCode = "301",
      description = "Redirects to URL",
      headers =
          @Header(
              name = "Location",
              description = "URL",
              schema = @Schema(implementation = String.class)))
  @APIResponse(responseCode = "404", description = "Redirect not found")
  public Response redirect(@PathParam("hash") @NotBlank String hash) {
    return redirectUseCase.execute(hash).toResponse();
  }
}
