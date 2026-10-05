package tacos.messaging.kitchen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tacos.TacoOrder;

@Service
public class KafkaOrderMessagingService implements OrderMessagingService {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderMessagingService.class);

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final boolean messagingEnabled;

    public KafkaOrderMessagingService(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
            @Value("${tacocloud.messaging.enabled:false}") boolean messagingEnabled) {
        this.kafkaTemplate = kafkaTemplate;
        this.messagingEnabled = messagingEnabled;
    }

    public void sendOrder(TacoOrder order) {
        if (!messagingEnabled) {
            log.debug("Kafka messaging is disabled. Order {} was not published.", order.getId());
            return;
        }

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getUser() == null ? "API" : order.getUser().getUsername(),
                order.getStatus(),
                order.getTotal(),
                order.getPlacedAt());
        kafkaTemplate.send("orders", event);
    }
}
