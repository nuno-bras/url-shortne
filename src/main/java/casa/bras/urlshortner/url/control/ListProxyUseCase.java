package casa.bras.urlshortner.url.control;

import casa.bras.urlshortner.common.errors.ApplicationError;
import casa.bras.urlshortner.common.errors.ApplicationException;
import casa.bras.urlshortner.url.dto.UrlProxyDTO;
import casa.bras.urlshortner.url.entity.UrlRepository;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import jakarta.enterprise.context.RequestScoped;
import java.util.List;
import java.util.UUID;

@RequestScoped
public class ListProxyUseCase {
  private final UserRepository userRepository;
  private final UrlRepository urlRepository;

  public ListProxyUseCase(UserRepository userRepository, UrlRepository urlRepository) {
    this.userRepository = userRepository;
    this.urlRepository = urlRepository;
  }

  public List<UrlProxyDTO> execute(UUID apiKey) {
    UserEntity user =
        userRepository
            .findByApiKey(apiKey)
            .orElseThrow(() -> new ApplicationException(ApplicationError.UNKNOWN_API_KEY));
    return urlRepository.findByUser(user).stream().map(UrlProxyDTO::new).toList();
  }
}
