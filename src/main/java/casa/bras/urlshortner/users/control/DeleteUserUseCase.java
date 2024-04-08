package casa.bras.urlshortner.users.control;

import casa.bras.urlshortner.users.entity.UserRepository;
import jakarta.enterprise.context.RequestScoped;
import org.bson.types.ObjectId;

@RequestScoped
public class DeleteUserUseCase {
  private final UserRepository repository;

  public DeleteUserUseCase(UserRepository repository) {
    this.repository = repository;
  }

  public void execute(String id) {
    repository.deleteById(new ObjectId(id));
  }
}
