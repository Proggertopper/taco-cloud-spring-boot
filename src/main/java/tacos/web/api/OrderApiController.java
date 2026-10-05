package tacos.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import tacos.OrderStatus;
import tacos.TacoOrder;
import tacos.data.OrderRepository;
import tacos.messaging.kitchen.OrderMessagingService;
import tacos.service.OrderAdminService;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping(path = "/api/orders", produces = "application/json")
public class OrderApiController {

    private final OrderRepository repository;
    private final OrderMessagingService orderMessagingService;
    private final OrderAdminService orderAdminService;

    OrderApiController(
            OrderRepository repository,
            OrderMessagingService orderMessagingService,
            OrderAdminService orderAdminService) {
        this.repository = repository;
        this.orderMessagingService = orderMessagingService;
        this.orderAdminService = orderAdminService;
    }

    @GetMapping
    public Iterable<TacoOrder> allOrders() {
        return repository.findAllByOrderByPlacedAtDesc();
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TacoOrder postOrder(@Valid @RequestBody TacoOrder order) {
        order.setStatus(OrderStatus.NEW);
        TacoOrder saved = repository.save(order);
        orderMessagingService.sendOrder(saved);
        return saved;
    }

    @PatchMapping("/{orderId}/status")
    public TacoOrder updateStatus(@PathVariable String orderId, @RequestParam OrderStatus status) {
        orderAdminService.updateStatus(orderId, status);
        return repository.findById(orderId).orElseThrow();
    }
}
