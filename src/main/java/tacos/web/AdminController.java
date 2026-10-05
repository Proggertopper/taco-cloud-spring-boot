package tacos.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tacos.OrderStatus;
import tacos.service.OrderAdminService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private OrderAdminService adminService;

    public AdminController(OrderAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public String adminPage(@RequestParam(required = false) OrderStatus status, Model model) {
        model.addAttribute("orders", adminService.findOrders(status));
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("selectedStatus", status);
        return "admin";
    }

    @PostMapping("/orders/{orderId}/status")
    public String updateStatus(@PathVariable String orderId, @RequestParam OrderStatus status) {
        adminService.updateStatus(orderId, status);
        return "redirect:/admin";
    }

    @PostMapping("/deleteOrders")
    public String deleteAllOrders() {
        adminService.deleteAllOrders();
        return "redirect:/";
    }
}
