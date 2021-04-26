package api.services;

import api.services.annotation.MuService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Flowable;

@MuService
@Client(id = "events")
@Secured(SecurityRule.IS_ANONYMOUS)
public interface EventsService {
    @Post("/events")
    Flowable<HttpResponse<byte[]>> trackEvents(@Body Flowable<byte[]> body);
}
