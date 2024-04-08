package casa.bras.urlshortner.users.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.bson.types.ObjectId;

public class UserEntity extends PanacheMongoEntity {
  @Size(max = 255)
  @Email
  private String email;

  @Size(max = 255)
  private String name;

  private UUID apiKey;

  public UserEntity() {}

  public UserEntity(String email, String name) {
    this.email = email;
    this.name = name;
    this.apiKey = UUID.randomUUID();
  }

  public UserEntity(ObjectId id, String email, String name) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.apiKey = UUID.randomUUID();
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UUID getApiKey() {
    return apiKey;
  }

  public void setApiKey(UUID apiKey) {
    this.apiKey = apiKey;
  }
}
