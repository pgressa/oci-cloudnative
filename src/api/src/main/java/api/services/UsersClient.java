package api.services;

import api.model.AddressInfo;
import api.model.CardInfo;
import api.model.MuUserDetails;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Client(id = ServiceLocator.USER, path = "/customers")
public interface UsersClient {
    @Get("/{customerId}")
    Maybe<byte[]> getUser(String customerId);

    @Post("/{customerId}/addresses")
    Single<AddressInfo> addAddress(String customerId, @Body AddressInfo address);

    @Get("/{customerId}/addresses")
    Flowable<AddressInfo> getAddresses(String customerId);

    @Post("/{customerId}/cards")
    Single<CardInfo> addCard(String customerId, @Body CardInfo card);

    @Get("/{customerId}/cards")
    Flowable<CardInfo> getCards(String customerId);

    @Get("/{customerId}")
    Single<MuUserDetails> getProfile(String customerId);
}
