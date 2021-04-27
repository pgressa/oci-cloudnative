package api.services;

import api.services.annotation.MuService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.session.Session;
import io.micronaut.session.annotation.SessionValue;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@MuService
@Secured(SecurityRule.IS_AUTHENTICATED)
public class CartsService {
    public static final String CART_ID = "cartId";
    private final CartsClient client;

    public CartsService(CartsClient client) {
        this.client = client;
    }

    @Get(value = "/cart", produces = MediaType.APPLICATION_JSON)
    Single<List<?>> getCart(@SessionValue(CART_ID) UUID cartID) {
        return client.getCartItems(cartID)
                .onErrorReturnItem(Collections.emptyList());
    }

    @Delete(value = "/cart", produces = MediaType.APPLICATION_JSON)
    Maybe<byte[]> deleteCart(@SessionValue(CART_ID) UUID cartID) {
        return client.deleteCart(cartID);
    }

    @Delete(value = "/cart/{id}", produces = MediaType.APPLICATION_JSON)
    Maybe<byte[]> deleteCartItem(@SessionValue(CART_ID) UUID cartID, String id) {
        return client.deleteCartItem(cartID, id);
    }

    @Client(id = "carts", path = "/carts")
    interface CartsClient {
        @Get(uri = "/{cartId}", produces = MediaType.APPLICATION_JSON)
        Maybe<byte[]> getCart(UUID cartId);

        @Get(uri = "/{cartId}/items", produces = MediaType.APPLICATION_JSON)
        Single<List<?>> getCartItems(UUID cartId);

        @Delete(uri = "/{cartId}", produces = MediaType.APPLICATION_JSON)
        Maybe<byte[]> deleteCart(UUID cartId);

        @Delete(uri = "/{cartId}/items/{itemId}", produces = MediaType.APPLICATION_JSON)
        Maybe<byte[]> deleteCartItem(UUID cartId, String itemId);

        @Post(uri = "/{cartId}", processes = MediaType.APPLICATION_JSON)
        Maybe<byte[]> postCart(UUID cartId, @Body byte[] body);

        @Put(uri = "/{cartId}/items", processes = MediaType.APPLICATION_JSON)
        Maybe<byte[]> updateCartItem(UUID cartId, @Body byte[] body);
    }
}
