package casa.bras.urlshortner.users.control;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ApplicationException;
import casa.bras.urlshortner.users.dto.CreateUserDTO;
import casa.bras.urlshortner.users.dto.UserDTO;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CreateUserUseCase {
  private final UserRepository repository;

  public CreateUserUseCase(UserRepository repository) {
    this.repository = repository;
  }

  public UserDTO execute(CreateUserDTO request) {

    if (repository.emailExists(request.getEmail())) {
      throw new ApplicationException(ApplicationError.CREATION_USER_DUPLICATED_EMAIL);
    }

    var user = repository.save(new UserEntity(request.getEmail(), request.getName()));
    return new UserDTO(user);
  }
}
