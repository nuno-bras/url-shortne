package casa.bras.urlshortner.users.boundary;

import casa.bras.urlshortner.users.control.CreateUserUseCase;
import casa.bras.urlshortner.users.control.DeleteUserUseCase;
import casa.bras.urlshortner.users.control.ViewUserUseCase;
import casa.bras.urlshortner.users.dto.CreateUserDTO;
import casa.bras.urlshortner.users.dto.UserDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/users")
@Tag(name = "Users")
public class UserResource {
  private final CreateUserUseCase createUserUseCase;
  private final ViewUserUseCase viewUserUseCase;
  private final DeleteUserUseCase deleteUserUseCase;

  public UserResource(
      CreateUserUseCase createUserUseCase,
      ViewUserUseCase viewUserUseCase,
      DeleteUserUseCase deleteUserUseCase) {
    this.createUserUseCase = createUserUseCase;
    this.viewUserUseCase = viewUserUseCase;
    this.deleteUserUseCase = deleteUserUseCase;
  }

  @POST
  @Operation(summary = "Creates a new user")
  @APIResponse(
      responseCode = "201",
      description = "User created",
      content = @Content(schema = @Schema(implementation = UserDTO.class)))
  @APIResponse(responseCode = "400", description = "Email already in use")
  public Response create(@Valid @NotNull CreateUserDTO request) {
    UserDTO user = createUserUseCase.execute(request);
    return Response.created(URI.create("/users/" + user.id())).entity(user).build();
  }

  @GET
  @Path("/{id}")
  @Operation(summary = "Creates a new user")
  @APIResponse(
      responseCode = "200",
      description = "User view returned",
      content = @Content(schema = @Schema(implementation = UserDTO.class)))
  @APIResponse(responseCode = "401", description = "Unauthorized")
  public UserDTO get(@PathParam("id") String id) {
    return viewUserUseCase.execute(id);
  }

  @DELETE
  @Path("/{id}")
  @Operation(summary = "Creates a new user")
  @APIResponse(responseCode = "204", description = "User deleted")
  @APIResponse(responseCode = "400", description = "Unauthorized")
  public void delete(@PathParam("id") String id) {
    deleteUserUseCase.execute(id);
  }
}
