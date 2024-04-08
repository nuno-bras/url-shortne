package casa.bras.urlshortner.common.errors;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<ApplicationException> {
  @Override
  public Response toResponse(ApplicationException exception) {
    return new ErrorDTO(exception).toResponse();
  }
}
