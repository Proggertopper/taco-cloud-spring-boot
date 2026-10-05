package tacos.messaging.kitchen;

import java.math.BigDecimal;
import java.util.Date;
import tacos.OrderStatus;

public record OrderCreatedEvent(
        String orderId,
        String username,
        OrderStatus status,
        BigDecimal total,
        Date placedAt) {
}
