package tacos.service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tacos.OrderStatus;
import tacos.TacoOrder;
import tacos.User;
import tacos.data.OrderRepository;
import tacos.messaging.kitchen.OrderMessagingService;
import tacos.web.OrderProps;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMessagingService orderMessagingService;
    private final OrderProps orderProps;

    public OrderService(
            OrderRepository orderRepository,
            OrderMessagingService orderMessagingService,
            OrderProps orderProps) {
        this.orderRepository = orderRepository;
        this.orderMessagingService = orderMessagingService;
        this.orderProps = orderProps;
    }

    public List<TacoOrder> findOrdersFor(User user) {
        return orderRepository.findByUserOrderByPlacedAtDesc(user, PageRequest.of(0, orderProps.getPageSize()));
    }

    public TacoOrder placeOrder(TacoOrder order, User user) {
        order.setUser(user);
        order.setStatus(OrderStatus.NEW);
        TacoOrder saved = orderRepository.save(order);
        orderMessagingService.sendOrder(saved);
        return saved;
    }

    public boolean cancelOrder(String orderId, User user) {
        return orderRepository.findById(orderId)
                .filter(order -> order.getUser() != null && order.getUser().getId().equals(user.getId()))
                .filter(order -> order.getStatus() == OrderStatus.NEW)
                .map(order -> {
                    order.setStatus(OrderStatus.CANCELLED);
                    orderRepository.save(order);
                    return true;
                })
                .orElse(false);
    }
}
