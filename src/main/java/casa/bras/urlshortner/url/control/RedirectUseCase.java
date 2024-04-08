package casa.bras.urlshortner.url.control;

import casa.bras.urlshortner.url.dto.ProxyDTO;
import casa.bras.urlshortner.url.entity.UrlRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class RedirectUseCase {
  private final UrlRepository repository;

  public RedirectUseCase(final UrlRepository repository) {
    this.repository = repository;
  }

  public ProxyDTO execute(String hash) {
    return new ProxyDTO(repository.findByHash(hash));
  }
}
