package api;

import api.dto.UserRegistrationRequest;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.CookieValue;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.session.SessionLoginHandler;
import io.micronaut.session.http.HttpSessionConfiguration;
import io.micronaut.session.http.HttpSessionFilter;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsersServiceTest extends AbstractDatabaseServiceTest {

    @Test
    void testShouldFailLogin(UserApiClient client) {
        HttpClientResponseException error = assertThrows(HttpClientResponseException.class, () ->
                client.login("junk", "junk")
        );
        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatus());
    }

    @Test
    void testRegister(UserApiClient client) {
        final Map<String, Object> result = client.register(new UserRegistrationRequest(
                "fred",
                "testpass",
                "Fred",
                "Flintstone",
                "fred@flinstones.com"
        ));
        assertNotNull(result);
        assertEquals(true, result.get("authenticated"));

        final HttpResponse<?> loginResult = client.login("fred", "testpass");
        assertEquals(HttpStatus.SEE_OTHER, loginResult.getStatus());
        assertTrue(loginResult.getHeaders().contains(HttpHeaders.AUTHORIZATION_INFO));
        assertTrue(loginResult.getHeaders().contains(HttpHeaders.SET_COOKIE));

        final Cookie session = loginResult.getCookie(HttpSessionConfiguration.DEFAULT_COOKIENAME).get();
        final String sessionID = session.getValue();
        final Map<String, Object> profile = client.getProfile(sessionID);

        assertNotNull(profile);

        client.logout(sessionID);

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                assertThrows(HttpClientResponseException.class, () -> client.getProfile(sessionID)).getStatus()
        );
    }

    @Override
    protected String getServiceId() {
        return "user";
    }

    @Client("/api")
    interface UserApiClient {
        @Get("/profile")
        Map<String, Object> getProfile(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);

        @Post("/login")
        HttpResponse<?> login(String username, String password);

        @Get("/logout")
        HttpResponse<?> logout(@CookieValue(HttpSessionConfiguration.DEFAULT_COOKIENAME) String sessionID);

        @Post("/register")
        Map<String, Object> register(@Body UserRegistrationRequest request);
    }
}
