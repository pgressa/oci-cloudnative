package mushop.orders.services;

import io.micronaut.nats.annotation.NatsClient;
import io.micronaut.nats.annotation.Subject;
import mushop.orders.values.OrderUpdate;

@NatsClient
public interface OrdersPublisher {

    @Subject("mushop-orders")
    void dispatchToFulfillment(OrderUpdate orderUpdate);
}
