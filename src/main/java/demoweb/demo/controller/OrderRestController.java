package demoweb.demo.controller;

import demoweb.demo.entity.Order;
import demoweb.demo.entity.OrderStatus;
import demoweb.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderRestController {

    private final OrderService orderService;

    @Autowired
    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{customerId}")
    public List<Order> getOrdersByCustomer(
            @PathVariable Integer customerId,
            @RequestParam(required = false) String status) {

        if (status == null || status.equalsIgnoreCase("all")) {
            return orderService.getOrdersByCustomer(customerId);
        }

        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return orderService.getOrdersByStatus(customerId, orderStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }
    }
}
