package casa.bras.urlshortner.url.boundary;

import casa.bras.urlshortner.url.control.CreateProxyUseCase;
import casa.bras.urlshortner.url.control.DeleteProxyUseCase;
import casa.bras.urlshortner.url.control.ListProxyUseCase;
import casa.bras.urlshortner.url.dto.UrlProxyDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.hibernate.validator.constraints.URL;

@Path("/urls")
@Tag(name = "URLs")
public class UrlProxyResource {
  protected static final String API_KEY_HEADER = "x-api-key";
  private final CreateProxyUseCase createProxyUseCase;
  private final ListProxyUseCase listProxyUseCase;
  private final DeleteProxyUseCase deleteProxyUseCase;

  public UrlProxyResource(
      final CreateProxyUseCase createProxyUseCase,
      final ListProxyUseCase listProxyUseCase,
      final DeleteProxyUseCase deleteProxyUseCase) {
    this.createProxyUseCase = createProxyUseCase;
    this.listProxyUseCase = listProxyUseCase;
    this.deleteProxyUseCase = deleteProxyUseCase;
  }

  @POST
  @Operation(summary = "Creates a new url")
  @APIResponse(
      responseCode = "201",
      description = "Proxy created",
      content = @Content(schema = @Schema(implementation = UrlProxyDTO.class)))
  @APIResponse(
      responseCode = "401",
      description = "API KEY invalid",
      content = @Content(schema = @Schema(implementation = UrlProxyDTO.class)))
  public Response create(
      @FormParam("url") @URL String url, @FormParam(API_KEY_HEADER) @NotNull UUID header) {

    UrlProxyDTO proxy = createProxyUseCase.execute(url, header);
    return Response.created(URI.create(proxy.hash())).entity(proxy).build();
  }

  @GET
  @Operation(summary = "Lists all urls")
  @APIResponse(
      responseCode = "200",
      description = "List of urls",
      content = @Content(schema = @Schema(implementation = UrlProxyDTO[].class)))
  @APIResponse(
      responseCode = "401",
      description = "API KEY invalid",
      content = @Content(schema = @Schema(implementation = UrlProxyDTO.class)))
  public List<UrlProxyDTO> list(@HeaderParam(API_KEY_HEADER) @NotNull UUID header) {
    return listProxyUseCase.execute(header);
  }

  @DELETE
  @Path("/{hash}")
  @Operation(summary = "Deletes a url")
  @APIResponse(responseCode = "204", description = "Proxy deleted")
  @APIResponse(
      responseCode = "401",
      description = "API KEY invalid",
      content = @Content(schema = @Schema(implementation = UrlProxyDTO.class)))
  public void delete(
      @PathParam("hash") String hash, @FormParam(API_KEY_HEADER) @NotNull UUID header) {
    deleteProxyUseCase.execute(hash, header);
  }
}
