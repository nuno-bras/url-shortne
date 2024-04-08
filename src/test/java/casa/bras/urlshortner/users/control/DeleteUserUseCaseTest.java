package casa.bras.urlshortner.users.control;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import casa.bras.urlshortner.users.entity.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class DeleteUserUseCaseTest {
  private final UserRepository userRepository = mock(UserRepository.class);
  private final DeleteUserUseCase useCase = new DeleteUserUseCase(userRepository);

  @Test
  void id_execute_sendsCommandToDeleteUserById() {
    var id = new ObjectId("bad471ec3dfc14bff415fcac");
    useCase.execute("bad471ec3dfc14bff415fcac");

    verify(userRepository).deleteById(id);
  }
}
