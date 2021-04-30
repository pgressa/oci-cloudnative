package api;

import api.AbstractDatabaseServiceTest.LoginClient;
import api.model.MuUserDetails;
import api.model.UserRegistrationRequest;
import api.services.AuthClient;
import api.model.Product;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.session.http.HttpSessionConfiguration;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartsServiceTest implements TestPropertyProvider {

    @Container
    static GenericContainer<?> cartsContainer = new GenericContainer<>(
            DockerImageName.parse("iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/carts:" + getServiceVersion())
    ).withExposedPorts(8080);

    private static String sessionID;

    private static String getServiceVersion() {
        return "1.0.1-SNAPSHOT";
    }

    @BeforeAll
    static void login(LoginClient client) {
        final HttpResponse<?> response = client.login("test", "password");
        final Cookie session = response.getCookie(HttpSessionConfiguration.DEFAULT_COOKIENAME).get();
        sessionID = session.getValue();
    }

    @Test
    @Order(1)
    void testReadNonExistentCart(CartClient client) {
        final List<Product> cart = client.getCart(sessionID);
        assertNotNull(cart);
    }

    @Test
    @Order(2)
    void testDeleteNonExistentCart(CartClient client) {
        final HttpStatus status = client.deleteCart(sessionID);
        assertNotNull(status);
        assertEquals(HttpStatus.NO_CONTENT, status);
    }

    @Test
    @Order(3)
    void testAddItemToCart(CartClient client) {
        final HttpStatus status = client.addItem(sessionID, Map.of(
                "id", 1234,
                "quantity", 2
        ));

        assertEquals(HttpStatus.CREATED, status);

        final List<Product> cart = client.getCart(sessionID);

        assertEquals(1, cart.size());

        final Product product = cart.iterator().next();
        assertEquals(10.00, product.price);

    }

    @Test
    @Order(4)
    void testDeleteItemFromCart(CartClient client) {
        List<Product> cart = client.getCart(sessionID);
        assertEquals(1, cart.size());
        final HttpStatus status = client.deleteCartItem(sessionID, "1234");
        assertEquals(HttpStatus.NO_CONTENT, status);

        cart = client.getCart(sessionID);
        assertEquals(0, cart.size());
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
        List<Product> getCart(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);

        @Delete
        HttpStatus deleteCart(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);

        @Delete("/{itemId}")
        HttpStatus deleteCartItem(
                @CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID,
                String itemId
        );

        @Post
        HttpStatus addItem(
                @CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID,
                @Body Map<String, Integer> item
        );
    }

    @MockBean(AuthClient.class)
    AuthClient authClient() {
        return new AuthClient() {
            @Override
            public Single<MuUserDetails> login(String username, String password) {
                return Single.just(new MuUserDetails(UUID.randomUUID().toString(), username));
            }

            @Override
            public Single<MuUserDetails> register(UserRegistrationRequest registrationRequest) {
                return Single.just(new MuUserDetails(UUID.randomUUID().toString(), registrationRequest.getUsername()));
            }
        };
    }

    @MockBean(api.services.CartsService.CatalogueClient.class)
    api.services.CartsService.CatalogueClient catalogueClient() {
        return id -> Maybe.just(new Product(id, 10.00));
    }
}
