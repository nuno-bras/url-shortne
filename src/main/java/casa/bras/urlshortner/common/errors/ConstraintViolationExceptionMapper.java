/*
 *
 * @Copyright 2022 TREIBAUF, S.A.
 * Development by VOID Software, SA
 *
 */
package casa.bras.urlshortner.common.errors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.*;

@Provider
public class ConstraintViolationExceptionMapper
    implements ExceptionMapper<ConstraintViolationException> {

  @Override
  public Response toResponse(ConstraintViolationException exception) {

    Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();

    StringBuilder errorMessageBuilder = new StringBuilder();
    if (constraintViolations != null && !constraintViolations.isEmpty()) {
      for (ConstraintViolation<?> violation : constraintViolations) {
        errorMessageBuilder.append(violation.getMessage()).append("; ");
      }
    }

    String errorMessage = errorMessageBuilder.toString();

    ApplicationError error = ApplicationError.VALIDATION_ERROR;
    ErrorDTO errorDTO = new ErrorDTO(error.ordinal(), errorMessage, error);

    return Response.status(error.getStatus().getStatusCode())
        .header("reason", "Object malformed")
        .entity(errorDTO)
        .build();
  }
}
