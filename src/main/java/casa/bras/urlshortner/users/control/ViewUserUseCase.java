package casa.bras.urlshortner.users.control;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ApplicationException;
import casa.bras.urlshortner.users.dto.UserDTO;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import jakarta.enterprise.context.RequestScoped;
import org.bson.types.ObjectId;

@RequestScoped
public class ViewUserUseCase {
  private final UserRepository repository;

  public ViewUserUseCase(UserRepository repository) {
    this.repository = repository;
  }

  public UserDTO execute(String id) {
    UserEntity user =
        repository
            .findByIdOptional(new ObjectId(id))
            .orElseThrow(() -> new ApplicationException(ApplicationError.USER_NOT_FOUND));
    return new UserDTO(user);
  }
}
