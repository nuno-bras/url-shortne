package casa.bras.urlshortner.users.control;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ApplicationException;
import casa.bras.urlshortner.users.dto.UserDTO;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ViewUserUseCaseTest {
  private final UserRepository userRepository = mock(UserRepository.class);
  private final ViewUserUseCase useCase = new ViewUserUseCase(userRepository);

  @Test
  void validEmail_execute_returnsUserDTO() {
    var id = new ObjectId("bad471ec3dfc14bff415fcac");
    var userEntity = new UserEntity(id, "email@email.com", "Test User");
    when(userRepository.findByIdOptional(id)).thenReturn(Optional.of(userEntity));

    UserDTO result = useCase.execute(id.toHexString());

    assertNotNull(result);
    assertEquals(userEntity.getEmail(), result.email());
    assertEquals(userEntity.getName(), result.name());

    verify(userRepository).findByIdOptional(id);
  }

  @Test
  void invalidEmail_execute_throwsException() {
    when(userRepository.findByIdOptional(any())).thenReturn(Optional.empty());

    var id = new ObjectId();
    var exception =
        assertThrows(ApplicationException.class, () -> useCase.execute(id.toHexString()));
    assertEquals(ApplicationError.USER_NOT_FOUND, exception.getError());

    verify(userRepository).findByIdOptional(id);
  }
}
