package com.prapps.retail.management.order;

import com.prapps.retail.management.order.Order;
import com.prapps.retail.management.exception.ResourceNotFound;
import com.prapps.retail.management.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String alive() {
        return "alive";
    }

    @PostMapping
    public Order create(Order order) {
        return orderService.create(order);
    }

    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order findOrder(@PathVariable("id") Long orderId) {
        return orderService.findById(orderId).orElseThrow(ResourceNotFound::new);
    }

    @GetMapping("/customer/{customerId}")
    public List<Order> findByCustomerId(@PathVariable("customerId") Long customerId) {
        return orderService.findOrderByCustomer(customerId);
    }
}
