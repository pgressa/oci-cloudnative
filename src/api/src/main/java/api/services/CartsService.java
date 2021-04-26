package api.services;

import api.services.annotation.MuService;
import io.micronaut.core.io.buffer.ByteBuffer;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.session.Session;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.UUID;

@MuService
@Secured(SecurityRule.IS_AUTHENTICATED)
public class CartsService {
    private final CartsClient client;

    public CartsService(CartsClient client) {
        this.client = client;
    }

    @Get(value = "/cart", produces = MediaType.APPLICATION_JSON)
    Maybe<ByteBuffer<?>> getCart(Session session) {
        final UUID cartId = getCartId(session);
        return client.getCartItems(cartId);
    }

    @Delete(value = "/cart", produces = MediaType.APPLICATION_JSON)
    Maybe<ByteBuffer<?>> deleteCart(Session session) {
        final UUID cartId = getCartId(session);
        return client.deleteCart(cartId);
    }

    @Delete(value = "/cart/{id}", produces = MediaType.APPLICATION_JSON)
    Maybe<ByteBuffer<?>> deleteCartItem(Session session, String id) {
        final UUID cartId = getCartId(session);
        return client.deleteCartItem(cartId, id);
    }


    private UUID getCartId(Session session) {
        return session.get("cartId", UUID.class).orElseGet(() -> {
            final UUID uuid = UUID.randomUUID();
            session.put("cartId", uuid);
            return uuid;
        });
    }

    @Client(id = "carts", path = "/carts")
    interface CartsClient {
        @Get(uri = "/{cartId}", produces = MediaType.APPLICATION_JSON)
        Maybe<ByteBuffer<?>> getCart(UUID cartId);

        @Get(uri = "/{cartId}/items", produces = MediaType.APPLICATION_JSON)
        Maybe<ByteBuffer<?>> getCartItems(UUID cartId);

        @Delete(uri = "/{cartId}", produces = MediaType.APPLICATION_JSON)
        Maybe<ByteBuffer<?>> deleteCart(UUID cartId);

        @Delete(uri = "/{cartId}/items/{itemId}", produces = MediaType.APPLICATION_JSON)
        Maybe<ByteBuffer<?>> deleteCartItem(UUID cartId, String itemId);

        @Post(uri = "/{cartId}", processes = MediaType.APPLICATION_JSON)
        Maybe<ByteBuffer<?>> postCart(UUID cartId, @Body Single<ByteBuffer<?>> body);

        @Put(uri = "/{cartId}/items", processes = MediaType.APPLICATION_JSON)
        Maybe<ByteBuffer<?>> updateCartItem(UUID cartId, @Body Single<ByteBuffer<?>> body);
    }
}
