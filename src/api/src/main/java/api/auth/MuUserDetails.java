package api.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.security.authentication.UserDetails;

import java.util.Collections;
import java.util.Optional;

@Introspected
public class MuUserDetails extends UserDetails {
    public static final String ID = "id";
    private final String id;

    public MuUserDetails(String id, String username) {
        super(username, Collections.emptyList(), Collections.singletonMap(ID, id));
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    @JsonIgnore
    public Optional<UserDetails> getUserDetails() {
        return super.getUserDetails();
    }
}
