package casa.bras.urlshortner.url.control;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ApplicationException;
import casa.bras.urlshortner.url.entity.UrlRepository;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import jakarta.enterprise.context.RequestScoped;
import java.util.UUID;

@RequestScoped
public class DeleteProxyUseCase {
  private final UserRepository userRepository;
  private final UrlRepository urlRepository;

  public DeleteProxyUseCase(final UserRepository userRepository, UrlRepository urlRepository) {
    this.userRepository = userRepository;
    this.urlRepository = urlRepository;
  }

  public void execute(String hash, UUID apiKey) {
    UserEntity user =
        userRepository
            .findByApiKey(apiKey)
            .orElseThrow(() -> new ApplicationException(ApplicationError.UNKNOWN_API_KEY));
    urlRepository.findByUserAndHash(user, hash).ifPresent(urlRepository::delete);
  }
}
