package casa.bras.urlshortner.url.control;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ApplicationException;
import casa.bras.urlshortner.url.dto.UrlProxyDTO;
import casa.bras.urlshortner.url.entity.UrlEntity;
import casa.bras.urlshortner.url.entity.UrlRepository;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import jakarta.enterprise.context.RequestScoped;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@RequestScoped
public class CreateProxyUseCase {
  private final UserRepository userRepository;
  private final UrlRepository urlRepository;

  public CreateProxyUseCase(UserRepository userRepository, UrlRepository urlRepository) {
    this.userRepository = userRepository;
    this.urlRepository = urlRepository;
  }

  public UrlProxyDTO execute(String url, UUID apiKey) {
    try {
      UserEntity user =
          userRepository
              .findByApiKey(apiKey)
              .orElseThrow(() -> new ApplicationException(ApplicationError.UNKNOWN_API_KEY));
      UrlEntity urlEntity = new UrlEntity(new URL(url), user);
      urlRepository.persist(urlEntity);
      return new UrlProxyDTO(urlEntity);
    } catch (MalformedURLException e) {
      throw new ApplicationException(ApplicationError.URL_FORMAT_INVALID);
    }
  }
}
