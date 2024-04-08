package casa.bras.urlshortner.url.control;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ApplicationException;
import casa.bras.urlshortner.url.entity.UrlEntity;
import casa.bras.urlshortner.url.entity.UrlRepository;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class DeleteProxyUseCaseTest {
  private final UserRepository userRepository = mock(UserRepository.class);
  private final UrlRepository urlRepository = mock(UrlRepository.class);
  private final DeleteProxyUseCase useCase = new DeleteProxyUseCase(userRepository, urlRepository);

  @Test
  void validHashAndApiKey_execute_deletesUrl() {
    UserEntity user = new UserEntity();
    var uuid = UUID.randomUUID();
    var hash = "someHash";

    when(userRepository.findByApiKey(uuid)).thenReturn(Optional.of(user));
    when(urlRepository.findByUserAndHash(user, hash)).thenReturn(Optional.of(new UrlEntity()));

    useCase.execute(hash, uuid);

    verify(userRepository).findByApiKey(uuid);
    verify(urlRepository).findByUserAndHash(user, hash);
    verify(urlRepository).delete(any(UrlEntity.class));
  }

  @Test
  void invalidHash_execute_doesNothing() {
    var uuid = UUID.randomUUID();
    var hash = "invalidHash";

    when(userRepository.findByApiKey(uuid)).thenReturn(Optional.of(new UserEntity()));
    when(urlRepository.findByUserAndHash(any(UserEntity.class), eq(hash)))
        .thenReturn(Optional.empty());

    useCase.execute(hash, uuid);

    verify(userRepository).findByApiKey(uuid);
    verify(urlRepository).findByUserAndHash(any(UserEntity.class), eq(hash));
    verify(urlRepository, never()).delete(any(UrlEntity.class));
  }

  @Test
  void userNotFound_execute_throwsException() {
    var uuid = UUID.randomUUID();
    var hash = "someHash";

    when(userRepository.findByApiKey(uuid)).thenReturn(Optional.empty());

    var exception = assertThrows(ApplicationException.class, () -> useCase.execute(hash, uuid));
    assertEquals(ApplicationError.UNKNOWN_API_KEY, exception.getError());

    verify(userRepository).findByApiKey(uuid);
    verify(urlRepository, never()).findByUserAndHash(any(UserEntity.class), anyString());
    verify(urlRepository, never()).delete(any(UrlEntity.class));
  }
}
