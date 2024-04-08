package casa.bras.urlshortner.url.dto;

import casa.bras.urlshortner.url.entity.UrlEntity;

public record UrlProxyDTO(String hash, String url) {
  public UrlProxyDTO(UrlEntity entity) {
    this(entity.getHash(), entity.getUrl());
  }
}
