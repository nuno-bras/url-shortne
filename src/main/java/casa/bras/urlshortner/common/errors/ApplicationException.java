package casa.bras.urlshortner.common.errors;

public class ApplicationException extends RuntimeException {
  private final ApplicationError error;
  private final String errorMessage;

  public ApplicationException(ApplicationError error) {
    this.error = error;
    this.errorMessage = error.getMessage();
  }

  public ApplicationException(Throwable throwable) {
    this.error = ApplicationError.GENERIC;
    this.errorMessage = throwable.getMessage();
  }

  public ApplicationError getError() {
    return error;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
