package casa.bras.urlshortner.url.control;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ApplicationException;
import casa.bras.urlshortner.url.dto.UrlProxyDTO;
import casa.bras.urlshortner.url.entity.UrlEntity;
import casa.bras.urlshortner.url.entity.UrlRepository;
import casa.bras.urlshortner.url.utils.UrlHashUtils;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@Tag("unit")
class CreateProxyUseCaseTest {
  private final UserRepository userRepository = mock(UserRepository.class);
  private final UrlRepository urlRepository = mock(UrlRepository.class);
  private final CreateProxyUseCase useCase = new CreateProxyUseCase(userRepository, urlRepository);

  @Test
  void validRequestAndApiKey_execute_createsNewUrlAndReturnsView() throws MalformedURLException {
    UserEntity user = new UserEntity();
    var uuid = UUID.randomUUID();
    when(userRepository.findByApiKey(any())).thenReturn(Optional.of(user));

    String url = "http://localhost";
    UrlProxyDTO result = useCase.execute(url, uuid);

    assertEquals(UrlHashUtils.hash(new URL(url)), result.hash());

    verify(userRepository).findByApiKey(uuid);
    var argumentCaptor = ArgumentCaptor.forClass(UrlEntity.class);
    verify(urlRepository).persist(argumentCaptor.capture());
    List<UrlEntity> urlEntities = argumentCaptor.getAllValues();
    assertEquals(1, urlEntities.size());
    assertEquals(url, urlEntities.get(0).getUrl());
    assertEquals(result.hash(), urlEntities.get(0).getHash());
  }

  @Test
  void invalidUUID_execute_throwsUserNotFoundException() {
    when(userRepository.findByApiKey(any())).thenReturn(Optional.empty());
    String url = "http://localhost";

    var exception =
        assertThrows(ApplicationException.class, () -> useCase.execute(url, UUID.randomUUID()));
    assertEquals(ApplicationError.UNKNOWN_API_KEY, exception.getError());

    verify(userRepository).findByApiKey(any());
    verify(urlRepository, never()).persist(any(UrlEntity.class));
  }

  @Test
  void invalidUrl_execute_throwsUrlFormatInvalidException() {
    when(userRepository.findByApiKey(any())).thenReturn(Optional.of(new UserEntity()));
    String url = "localhost";

    var exception =
        assertThrows(ApplicationException.class, () -> useCase.execute(url, UUID.randomUUID()));
    assertEquals(ApplicationError.URL_FORMAT_INVALID, exception.getError());

    verify(userRepository).findByApiKey(any());
    verify(urlRepository, never()).persist(any(UrlEntity.class));
  }
}
