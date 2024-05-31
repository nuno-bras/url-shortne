package casa.bras.urlshortner.view.boundary;

import casa.bras.urlshortner.url.entity.UrlRepository;
import casa.bras.urlshortner.users.dto.UserDTO;
import casa.bras.urlshortner.users.entity.UserEntity;
import casa.bras.urlshortner.users.entity.UserRepository;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.bson.types.ObjectId;

@Path("/ui")
@Produces(MediaType.TEXT_HTML)
public class HomeResource {

  private final Template index;
  private final Template user;
  private final UserRepository userRepository;
  private final UrlRepository urlRepository;

  public HomeResource(
      Template index, Template user, UserRepository userRepository, UrlRepository urlRepository) {
    this.index = index;
    this.user = user;
    this.userRepository = userRepository;
    this.urlRepository = urlRepository;
  }

  @GET
  public TemplateInstance home() {
    return index.instance();
  }

  @GET
  @Path("{userId}")
  public TemplateInstance dashboard(@PathParam("userId") String userId) {
    UserEntity user = userRepository.findById(new ObjectId(userId));
    return this.user.data("user", new UserDTO(user)).data("urls", urlRepository.findByUser(user));
  }
}
