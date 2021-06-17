package api.services.support;

import api.services.annotation.CartId;
import io.micronaut.configuration.lettuce.session.RedisSessionStore;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.AnnotatedRequestArgumentBinder;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.session.Session;
import io.micronaut.session.SessionStore;
import io.micronaut.session.http.HttpSessionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Singleton
public class CartIdBinder implements AnnotatedRequestArgumentBinder<CartId, UUID> {
    private static final Logger LOG = LoggerFactory.getLogger(CartIdBinder.class);
    public static final String CART_ID = "cartId";

    private final RedisSessionStore sessionStore;

    public CartIdBinder(RedisSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Override
    public Class<CartId> getAnnotationType() {
        return CartId.class;
    }

    @Override
    public BindingResult<UUID> bind(ArgumentConversionContext<UUID> context, HttpRequest<?> source) {
        LOG.info("Binding CartID request " + source.getHeaders().asMap().entrySet().stream().map(e -> e.getKey() + " -> [ " + String.join(",", e.getValue()) + "]").collect(Collectors.joining(",")));
        Optional<Cookie> sessionCookie = source.getCookies().findCookie("SESSION");
        if (sessionCookie.isPresent()) {
            LOG.trace("WE FOUND SESSION COOKIE {}", sessionCookie.get().getValue());
        }
        final Optional<Session> attribute = source.getAttribute(HttpSessionFilter.SESSION_ATTRIBUTE, Session.class);
        if (attribute.isPresent()) {
            final Session session = attribute.get();
            final UUID uuid = session.get(CART_ID, UUID.class).orElseGet(() -> {
                final UUID newUUID = UUID.randomUUID();
                LOG.info("Generating new session ID " + newUUID);
                session.put(CART_ID, newUUID);
                return newUUID;
            });
            return () -> Optional.of(uuid);
        } else {
            LOG.info("Session not found in the request");
            return BindingResult.UNSATISFIED;
        }
    }
}
