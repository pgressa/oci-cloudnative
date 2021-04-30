package api.services;

import api.model.AddressInfo;
import api.model.CardInfo;
import api.model.MuUserDetails;
import api.model.UserRegistrationRequest;
import api.services.annotation.MuService;
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
    Single<MuUserDetails> register(HttpRequest<?> request, @Valid @Body UserRegistrationRequest registrationRequest) {
        return authClient.register(registrationRequest)
                .map((userDTO -> {
                    sessionLoginHandler.loginSuccess(userDTO, request);
                    return userDTO;
                }));
    }

    @Get("/profile")
    Maybe<byte[]> getProfile(Authentication auth) {
        return client.getUser(MuUserDetails.resolveId(auth));
    }

    @Get("/customers/{id}")
    Maybe<byte[]> getProfile(String id, Authentication auth) {
        final String authId = MuUserDetails.resolveId(auth);
        if (id.equals(authId)) {
            return client.getUser(authId);
        } else {
            return Maybe.error(new HttpStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }
    }

    @Post("/address")
    Single<AddressInfo> addAddress(Authentication authentication, @Body AddressInfo body) {
        return client.addAddress(MuUserDetails.resolveId(authentication), body);
    }

    @Get("/address")
    Single<AddressInfo> getAddress(Authentication authentication) {
        return client.getAddress(MuUserDetails.resolveId(authentication)).firstOrError();
    }

    @Post("/card")
    Single<CardInfo> addCard(Authentication authentication, @Body CardInfo body) {
        return client.addCard(MuUserDetails.resolveId(authentication), body);
    }

    @Get("/card")
    Single<Map<String, Object>> getCard(Authentication authentication) {
        return client.getCard(MuUserDetails.resolveId(authentication)).firstOrError();
    }

    @Client(id = "user", path = "/customers")
    interface UsersClient {
        @Get("/{customerId}")
        Maybe<byte[]> getUser(String customerId);

        @Post("/{customerId}/addresses")
        Single<AddressInfo> addAddress(String customerId, @Body AddressInfo address);

        @Get("/{customerId}/addresses")
        Flowable<AddressInfo> getAddress(String customerId);

        @Post("/{customerId}/cards")
        Single<CardInfo> addCard(String customerId, @Body CardInfo card);

        @Get("/{customerId}/cards")
        Flowable<Map<String, Object>> getCard(String customerId);
    }

}
