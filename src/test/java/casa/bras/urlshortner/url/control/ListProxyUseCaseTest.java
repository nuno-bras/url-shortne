package casa.bras.urlshortner.url.control;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ApplicationException;
import casa.bras.urlshortner.url.dto.UrlProxyDTO;
import casa.bras.urlshortner.url.entity.UrlEntity;
import casa.bras.urlshortner.url.entity.UrlRepository;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ListProxyUseCaseTest {
  private final UserRepository userRepository = mock(UserRepository.class);
  private final UrlRepository urlRepository = mock(UrlRepository.class);
  private final ListProxyUseCase useCase = new ListProxyUseCase(userRepository, urlRepository);

  @Test
  void validApiKey_execute_returnsListOfUrls() throws MalformedURLException {
    UserEntity user = new UserEntity();
    var uuid = UUID.randomUUID();
    List<UrlEntity> urlEntities =
        Arrays.asList(
            new UrlEntity(new URL("http://localhost"), user),
            new UrlEntity(new URL("http://localhost:8080"), user));

    when(userRepository.findByApiKey(uuid)).thenReturn(Optional.of(user));
    when(urlRepository.findByUser(user)).thenReturn(urlEntities);

    List<UrlProxyDTO> result = useCase.execute(uuid);

    assertEquals(urlEntities.size(), result.size());
    assertEquals(urlEntities.get(0).getUrl(), result.get(0).url());
    assertEquals(urlEntities.get(0).getHash(), result.get(0).hash());
    assertEquals(urlEntities.get(1).getUrl(), result.get(1).url());
    assertEquals(urlEntities.get(1).getHash(), result.get(1).hash());

    verify(userRepository).findByApiKey(uuid);
    verify(urlRepository).findByUser(user);
  }

  @Test
  void userNotFound_execute_throwsException() {
    var uuid = UUID.randomUUID();

    when(userRepository.findByApiKey(uuid)).thenReturn(Optional.empty());

    var exception = assertThrows(ApplicationException.class, () -> useCase.execute(uuid));
    assertEquals(ApplicationError.UNKNOWN_API_KEY, exception.getError());

    verify(userRepository).findByApiKey(uuid);
    verify(urlRepository, never()).findByUser(any(UserEntity.class));
  }

  @Test
  void noUrlsForUser_execute_returnsEmptyList() {
    UserEntity user = new UserEntity();
    var uuid = UUID.randomUUID();

    when(userRepository.findByApiKey(uuid)).thenReturn(Optional.of(user));
    when(urlRepository.findByUser(user)).thenReturn(Collections.emptyList());

    List<UrlProxyDTO> result = useCase.execute(uuid);

    assertTrue(result.isEmpty());

    verify(userRepository).findByApiKey(uuid);
    verify(urlRepository).findByUser(user);
  }
}
