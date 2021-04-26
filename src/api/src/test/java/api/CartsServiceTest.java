package api;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CartsServiceTest implements TestPropertyProvider {

    @Container
    static GenericContainer<?> cartsContainer = new GenericContainer<>(
            DockerImageName.parse("iad.ocir.io/oracle/ateam/mushop-carts:1.2.0")
    ).withExposedPorts(80);

    @Inject
    CartClient client;

    @Test
    void testReadCart() {
        final List<Map<String, Object>> cart = client.getCart();

        assertNotNull(cart);
    }

    @Nonnull
    @Override
    public Map<String, String> getProperties() {
        return Collections.singletonMap(
                "micronaut.http.services.carts.url", "http://localhost:" + cartsContainer.getFirstMappedPort()
        );
    }

    @Client("/api/cart")
    interface CartClient {
        @Get
        List<Map<String, Object>> getCart();
    }
}
