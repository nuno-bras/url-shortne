package casa.bras.urlshortner.common.errors;

import jakarta.ws.rs.core.Response;

public enum ApplicationError {
  GENERIC(Response.Status.INTERNAL_SERVER_ERROR, "Generic Server error"),
  CREATION_USER_INVALID_EMAIL(Response.Status.BAD_REQUEST, "Invalid email"),
  CREATION_USER_DUPLICATED_EMAIL(Response.Status.BAD_REQUEST, "Duplicate email"),
  CREATION_PROXY_INVALID_URL(Response.Status.BAD_REQUEST, "Invalid URL"),
  UNKNOWN_API_KEY(Response.Status.UNAUTHORIZED, "Unknown API KEY"),
  USER_NOT_FOUND(Response.Status.NOT_FOUND, "User not found"),
  URL_FORMAT_INVALID(Response.Status.BAD_REQUEST, "URL format invalid"),
  VALIDATION_ERROR(Response.Status.BAD_REQUEST, "Malformed Object");

  private final Response.Status status;
  private final String message;

  ApplicationError(final Response.Status status, final String message) {
    this.status = status;
    this.message = message;
  }

  public Response.Status getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
