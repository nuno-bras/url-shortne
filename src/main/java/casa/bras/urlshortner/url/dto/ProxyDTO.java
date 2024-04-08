package casa.bras.urlshortner.url.dto;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import java.net.URL;
import java.util.Optional;

public record ProxyDTO(Optional<URL> url) {
  private static final Response NOT_FOUND = Response.status(Response.Status.NOT_FOUND).build();

  public Response toResponse() {
    return url.map(
            url ->
                Response.status(Response.Status.MOVED_PERMANENTLY)
                    .header(HttpHeaders.LOCATION, url)
                    .build())
        .orElse(NOT_FOUND);
  }
}
