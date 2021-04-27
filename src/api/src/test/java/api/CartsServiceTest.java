package api;

import api.model.MuUserDetails;
import api.model.UserRegistrationRequest;
import api.services.AuthClient;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.CookieValue;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.session.http.HttpSessionConfiguration;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Single;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartsServiceTest extends AbstractDatabaseServiceTest {

    private static String sessionID;

    @BeforeAll
    static void login(LoginClient client) {
        final HttpResponse<?> response = client.login("test", "password");
        final Cookie session = response.getCookie(HttpSessionConfiguration.DEFAULT_COOKIENAME).get();
        sessionID = session.getValue();
    }


    @Test
    void testReadCart(CartClient client) {
        final List<Map<String, Object>> cart = client.getCart(sessionID);
        assertNotNull(cart);
    }

    @Override
    protected String getServiceVersion() {
        return "1.0.1-SNAPSHOT";
    }

    @Override
    protected String getServiceId() {
        return "carts";
    }

    @Client("/api/cart")
    interface CartClient {
        @Get
        List<Map<String, Object>> getCart(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);
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
}
