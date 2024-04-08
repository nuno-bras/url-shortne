package casa.bras.utilities;

import com.google.common.collect.ImmutableMap;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;
import org.testcontainers.containers.MongoDBContainer;

public class MongoResource implements QuarkusTestResourceLifecycleManager {

  private MongoDBContainer container;

  @Override
  public Map<String, String> start() {

    container = new MongoDBContainer("mongo:6.0.8");
    container.start();

    return ImmutableMap.of(
        "quarkus.mongodb.connection-string",
        container.getConnectionString() + "/?uuidRepresentation=standard",
        "quarkus.mongodb.database",
        "test");
  }

  @Override
  public void stop() {

    container.stop();
  }
}
