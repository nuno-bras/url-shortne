package casa.bras.urlshortner.common.errors;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public record ErrorDTO(int errorCode, String errorMessage, ApplicationError errorType) {
  public ErrorDTO(ApplicationException exception) {
    this(exception.getError().ordinal(), exception.getErrorMessage(), exception.getError());
  }

  Response toResponse() {
    return Response.status(errorType.getStatus())
        .entity(this)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
