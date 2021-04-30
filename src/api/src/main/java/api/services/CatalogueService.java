package api.services;

import api.services.annotation.MuService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.micronaut.security.rules.SecurityRule;

@MuService
@Client(id = "catalogue")
@Secured(SecurityRule.IS_ANONYMOUS)
public interface CatalogueService {
    @Get(uri = "/catalogue/images/{+path}", processes = MediaType.IMAGE_PNG)
    Flowable<HttpResponse<byte[]>> getImage(String path);

    @Get("/catalogue/{id}")
    Single<HttpResponse<byte[]>> getItem(String id);

    @Get("/categories")
    Single<HttpResponse<byte[]>> getCategories();
}
