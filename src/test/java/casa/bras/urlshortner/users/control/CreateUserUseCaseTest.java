package casa.bras.urlshortner.users.control;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ApplicationException;
import casa.bras.urlshortner.users.dto.CreateUserDTO;
import casa.bras.urlshortner.users.dto.UserDTO;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class CreateUserUseCaseTest {
  private final UserRepository userRepository = mock(UserRepository.class);
  private final CreateUserUseCase useCase = new CreateUserUseCase(userRepository);

  @Test
  void validRequest_execute_createsUserAndReturnsUserDTO() {
    var request = new CreateUserDTO("test@example.com", "Test User");
    var savedUser =
        new UserEntity(
            new ObjectId("bad471ec3dfc14bff415fcac"), request.getEmail(), request.getName());
    when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
    when(userRepository.emailExists(any())).thenReturn(false);

    UserDTO result = useCase.execute(request);

    assertNotNull(result);
    assertEquals(savedUser.getEmail(), result.email());
    assertEquals(savedUser.getName(), result.name());

    verify(userRepository).save(any(UserEntity.class));
    verify(userRepository).emailExists(request.getEmail());
  }

  @Test
  void duplicatedEmail_execute_throwsDuplicateEmailException() {
    var request = new CreateUserDTO("test@example.com", "Test User");
    when(userRepository.emailExists(any())).thenReturn(true);

    var exception = assertThrows(ApplicationException.class, () -> useCase.execute(request));
    assertEquals(ApplicationError.CREATION_USER_DUPLICATED_EMAIL, exception.getError());

    verify(userRepository, never()).save(any(UserEntity.class));
    verify(userRepository).emailExists(request.getEmail());
  }
}
