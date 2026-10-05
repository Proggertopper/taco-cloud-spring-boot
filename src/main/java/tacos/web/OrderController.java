package tacos.web;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import lombok.extern.slf4j.Slf4j;
import tacos.TacoOrder;
import tacos.User;
import tacos.service.OrderService;


@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping("/current")
    public String orderForm(@ModelAttribute TacoOrder tacoOrder, @AuthenticationPrincipal User user) {
        if (tacoOrder.getDeliveryName() == null && user != null) {
            tacoOrder.setDeliveryName(user.getFullname());
            tacoOrder.setDeliveryStreet(user.getStreet());
            tacoOrder.setDeliveryCity(user.getCity());
            tacoOrder.setDeliveryState(user.getState());
            tacoOrder.setDeliveryZip(user.getZip());
        }
        return "orderForm";
    }

    @GetMapping
    public String ordersForUser( @AuthenticationPrincipal User user , Model model) {
        model.addAttribute("orders", orderService.findOrdersFor(user));
        return "orderList";
    }

    @PostMapping
    public String processOrder( @Valid TacoOrder order, Errors errors ,
                                SessionStatus sessionStatus , @AuthenticationPrincipal User user) {
        if(errors.hasErrors()) {
            return "orderForm";
        }

        orderService.placeOrder(order, user);
        sessionStatus.setComplete();
        return "redirect:/";
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable String orderId, @AuthenticationPrincipal User user) {
        orderService.cancelOrder(orderId, user);
        return "redirect:/orders";
    }
}
