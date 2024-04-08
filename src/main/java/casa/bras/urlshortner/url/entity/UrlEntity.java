package casa.bras.urlshortner.url.entity;

import casa.bras.urlshortner.common.errors.ApplicationException;
import casa.bras.urlshortner.url.utils.UrlHashUtils;
import casa.bras.urlshortner.users.entity.UserEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import java.net.MalformedURLException;
import java.net.URL;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

public class UrlEntity extends PanacheMongoEntity {
  private String hash;
  private String url;
  private ObjectId userId;

  public UrlEntity() {}

  public UrlEntity(URL url, UserEntity user) {
    this.url = url.toString();
    this.userId = user.id;
    this.hash = UrlHashUtils.hash(url);
  }

  @BsonIgnore
  public URL getUrlAsObject() {
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new ApplicationException(e);
    }
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public ObjectId getUserId() {
    return userId;
  }

  public void setUserId(ObjectId userId) {
    this.userId = userId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }
}
