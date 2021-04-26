package api.services.annotation;

import io.micronaut.http.annotation.Controller;
import io.micronaut.retry.annotation.CircuitBreaker;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import api.services.exceptions.ClientResponsePredicate;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Retention(RetentionPolicy.RUNTIME)
@CircuitBreaker(predicate = ClientResponsePredicate.class)
@Controller("/api")
public @interface MuService {
}
