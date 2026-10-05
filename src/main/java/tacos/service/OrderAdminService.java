package tacos.service;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import tacos.OrderStatus;
import tacos.TacoOrder;
import tacos.data.OrderRepository;

@Service
public class OrderAdminService {

    private final OrderRepository orderRepository;

    OrderAdminService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_admin')")
    public List<TacoOrder> findOrders(OrderStatus status) {
        if (status == null) {
            return orderRepository.findAllByOrderByPlacedAtDesc();
        }
        return orderRepository.findByStatusOrderByPlacedAtDesc(status);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_admin')")
    public void updateStatus(String orderId, OrderStatus status) {
        TacoOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_admin')")
    public void deleteAllOrders() {
        orderRepository.deleteAll();
    }
}
