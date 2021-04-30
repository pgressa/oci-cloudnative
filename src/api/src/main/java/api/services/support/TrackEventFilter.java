package api.services.support;

import api.model.Event;
import api.services.EventsService;
import api.services.annotation.TrackEvent;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.session.Session;
import io.micronaut.session.http.HttpSessionFilter;
import io.micronaut.web.router.RouteMatch;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;


@Filter(Filter.MATCH_ALL_PATTERN)
public class TrackEventFilter implements HttpServerFilter {
    private final EventsService eventsService;

    public TrackEventFilter(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        RouteMatch<?> routeMatch = request.getAttribute(HttpAttributes.ROUTE_MATCH, RouteMatch.class).orElse(null);
        if (routeMatch != null) {
            final String event = routeMatch.getAnnotationMetadata().stringValue(TrackEvent.class).orElse(null);
            if (event != null) {
                return Flowable.fromPublisher(chain.proceed(request))
                            .flatMap(response -> {
                                final Session session
                                        = request.getAttribute(HttpSessionFilter.SESSION_ATTRIBUTE, Session.class)
                                                 .orElse(null);
                                if (session != null) {
                                    final Object body = response.body();
                                    final Event evt = new Event(event, body);
                                    return eventsService.trackEvents("api", session.getId(), evt)
                                            .onErrorComplete()
                                            .andThen(Flowable.just(response));
                                }
                                return Flowable.just(response);
                            });
            }
        }
        return chain.proceed(request);
    }
}
