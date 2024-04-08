package casa.bras.utilities;

import io.quarkus.test.common.QuarkusTestResource;
import org.junit.jupiter.api.Tag;

@QuarkusTestResource(value = MongoResource.class, restrictToAnnotatedClass = true)
@Tag("integration")
public abstract class IntegrationTest {}
