package casa.bras.urlshortner.authentication.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
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
class AuthenticationUseCaseTest {

  private final UserRepository userRepository = mock(UserRepository.class);
  private final AuthenticationUseCase authenticationUseCase =
      new AuthenticationUseCase(userRepository);

  @Test
  void correctEmailAndPassword_execute_returnsUserDTO() {
    UserEntity user = new UserEntity(new ObjectId(), "test@abc.com", "An user");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    UserDTO userDTO = authenticationUseCase.execute("test@abc.com", "password");
    assertEquals(user.getEmail(), userDTO.email());
    assertEquals(user.getName(), userDTO.name());
    assertEquals(user.getApiKey().toString(), userDTO.apiKey());

    verify(userRepository).findByEmail("test@abc.com");
  }

  @Test
  void incorrectEmailAndPassword_execute_throwsUnauthorizedException() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    ApplicationException exception =
        assertThrows(
            ApplicationException.class,
            () -> authenticationUseCase.execute("test@abc.com", "password"));
    assertEquals(exception.getError(), ApplicationError.UNAUTHORIZED);

    verify(userRepository).findByEmail("test@abc.com");
  }
}
