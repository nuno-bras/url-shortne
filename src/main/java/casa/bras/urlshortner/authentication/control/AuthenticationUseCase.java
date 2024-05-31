package casa.bras.urlshortner.authentication.control;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ApplicationException;
import casa.bras.urlshortner.users.dto.UserDTO;
import casa.bras.urlshortner.users.entity.UserRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AuthenticationUseCase {

  private final UserRepository userRepository;

  public AuthenticationUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDTO execute(String email, String password) {
    return userRepository
        .findByEmail(email)
        .map(UserDTO::new)
        .orElseThrow(() -> new ApplicationException(ApplicationError.UNAUTHORIZED));
  }
}
