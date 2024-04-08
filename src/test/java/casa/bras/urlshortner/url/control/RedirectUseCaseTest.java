package casa.bras.urlshortner.url.control;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import casa.bras.urlshortner.url.dto.ProxyDTO;
import casa.bras.urlshortner.url.entity.UrlRepository;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class RedirectUseCaseTest {
  private final UrlRepository urlRepository = mock(UrlRepository.class);
  private final RedirectUseCase useCase = new RedirectUseCase(urlRepository);

  @Test
  void validHash_execute_returnsProxyDTO() throws MalformedURLException {
    var hash = "someHash";
    when(urlRepository.findByHash(hash)).thenReturn(Optional.of(new URL("http://example.com")));

    ProxyDTO result = useCase.execute(hash);

    assertNotNull(result);
    assertEquals(Optional.of(new URL("http://example.com")), result.url());

    verify(urlRepository).findByHash(hash);
  }

  @Test
  void invalidHash_execute_returnsNull() {
    var hash = "invalidHash";
    when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

    ProxyDTO result = useCase.execute(hash);

    assertNotNull(result);
    assertEquals(new ProxyDTO(Optional.empty()), result);

    verify(urlRepository).findByHash(hash);
  }
}
