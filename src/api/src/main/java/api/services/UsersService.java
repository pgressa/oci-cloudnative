package api.services;

import api.dto.UserDTO;
import api.dto.UserRegistrationRequest;
import api.services.annotation.MuService;
import io.micronaut.core.io.buffer.ByteBuffer;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.session.SessionLoginHandler;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

@MuService
@Secured(SecurityRule.IS_AUTHENTICATED)
public class UsersService {
    private final UsersClient client;
    private final AuthClient authClient;
    private final SessionLoginHandler sessionLoginHandler;

    UsersService(UsersClient client, AuthClient authClient, SessionLoginHandler sessionLoginHandler) {
        this.client = client;
        this.authClient = authClient;
        this.sessionLoginHandler = sessionLoginHandler;
    }

    @Post("/register")
    @Secured(SecurityRule.IS_ANONYMOUS)
    Single<UserDTO> register(HttpRequest<?> request, @Valid @Body UserRegistrationRequest registrationRequest) {
        return authClient.register(registrationRequest)
                .map((userDTO -> {
                    sessionLoginHandler.loginSuccess(userDTO, request);
                    return userDTO;
                }));
    }

    @Get("/profile")
    Maybe<byte[]> getProfile(Authentication auth) {
        return client.getUser(resolveId(auth));
    }

    @Get("/customers/{id}")
    Maybe<byte[]> getProfile(String id, Authentication auth) {
        final String authId = resolveId(auth);
        if (id.equals(authId)) {
            return client.getUser(authId);
        } else {
            return Maybe.error(new HttpStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }
    }

    @Post("/address")
    Single<byte[]> addAddress(Authentication authentication, @Body Single<ByteBuffer<?>> body) {
        return client.addAddress(resolveId(authentication), body);
    }

    @Get("/address")
    Single<Map<String, Object>> addAddress(Authentication authentication) {
        return client.getAddress(resolveId(authentication)).firstOrError();
    }

    @Post("/card")
    Single<byte[]> addCard(Authentication authentication, @Body Single<ByteBuffer<?>> body) {
        return client.addCard(resolveId(authentication), body);
    }

    @Get("/card")
    Single<Map<String, Object>> getCard(Authentication authentication) {
        return client.getCard(resolveId(authentication)).firstOrError();
    }

    private String resolveId(Authentication auth) {
        return Objects.requireNonNull(auth.getAttributes().get(UserDTO.ID), "User ID should never be null")
                .toString();
    }

    @Client(id = "user", path = "/customers")
    interface UsersClient {
        @Get("/{customerId}")
        Maybe<byte[]> getUser(String customerId);

        @Post("/{customerId}/addresses")
        Single<byte[]> addAddress(String customerId, @Body Single<ByteBuffer<?>> address);

        @Get("/{customerId}/addresses")
        Flowable<Map<String, Object>> getAddress(String customerId);

        @Post("/{customerId}/cards")
        Single<byte[]> addCard(String customerId, @Body Single<ByteBuffer<?>> card);

        @Get("/{customerId}/cards")
        Flowable<Map<String, Object>> getCard(String customerId);
    }

}
