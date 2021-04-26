package api.services;

import api.dto.UserDTO;
import api.dto.UserRegistrationRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Single;

@Client(id = "user")
public interface AuthClient {
    @Post("/login")
    Single<UserDTO> login(String username, String password);

    @Post("/register")
    Single<UserDTO> register(@Body UserRegistrationRequest registrationRequest);
}
