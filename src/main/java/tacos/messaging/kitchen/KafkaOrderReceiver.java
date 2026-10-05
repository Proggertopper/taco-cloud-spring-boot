package tacos.messaging.kitchen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderReceiver {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderReceiver.class);

    @KafkaListener(topics = "orders", groupId = "tacoGroup")
    public void receiveOrder(OrderCreatedEvent event) {
        log.info("Kitchen received order {} from {} for {}", event.orderId(), event.username(), event.total());
    }
}
