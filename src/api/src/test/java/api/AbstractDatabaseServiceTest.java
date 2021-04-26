package api;

import io.micronaut.test.support.TestPropertyProvider;
import org.junit.jupiter.api.AfterAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.DockerImageName;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

abstract class AbstractDatabaseServiceTest implements TestPropertyProvider {
    static OracleContainer oracleContainer;

    static GenericContainer<?> serviceContainer;

    @AfterAll
    static void cleanup() {
        oracleContainer.stop();
        serviceContainer.stop();
    }

    @Nonnull
    @Override
    public Map<String, String> getProperties() {
        oracleContainer = new OracleContainer("registry.gitlab.com/micronaut-projects/micronaut-graal-tests/oracle-database:18.4.0-xe")
                .withNetwork(Network.SHARED)
                .withNetworkAliases("oracledb");
        oracleContainer.start();
        serviceContainer = new GenericContainer<>(
                DockerImageName.parse("iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/" + getServiceId() + ":1.0.0-SNAPSHOT")
        ).withExposedPorts(8080)
                .withNetwork(Network.SHARED)
                .withEnv(Map.of(
                        "DATASOURCES_DEFAULT_URL", "jdbc:oracle:thin:system/oracle@oracledb:1521:xe",
                        "DATASOURCES_DEFAULT_USERNAME", oracleContainer.getUsername(),
                        "DATASOURCES_DEFAULT_PASSWORD", oracleContainer.getPassword()
                ));
        serviceContainer.start();
        return Collections.singletonMap(
                "micronaut.http.services." + getServiceId() + ".url", "http://localhost:" + serviceContainer.getFirstMappedPort()
        );
    }

    protected abstract String getServiceId();
}
