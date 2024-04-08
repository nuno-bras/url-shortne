package casa.bras.urlshortner.users.dto;

import casa.bras.urlshortner.users.entity.UserEntity;

public record UserDTO(String id, String name, String email, String apiKey) {
  public UserDTO(UserEntity entity) {
    this(
        entity.id.toHexString(),
        entity.getName(),
        entity.getEmail(),
        entity.getApiKey().toString());
  }
}
