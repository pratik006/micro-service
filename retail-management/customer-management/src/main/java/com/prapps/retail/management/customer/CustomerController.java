package com.prapps.retail.management.customer;

import com.prapps.retail.management.customer.service.CustomerService;
import com.prapps.retail.management.exception.ResourceNotFound;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private CustomerService productService;

    public CustomerController(CustomerService orderService) {
        this.productService = orderService;
    }

    @GetMapping
    public String alive() {
        return "alive";
    }

    @PostMapping
    public Customer create(@RequestBody Customer product) {
        return productService.create(product);
    }

    @GetMapping("/{id}")
    public Customer findProductById(@PathVariable("id") Long productId) {
        return productService.findById(productId).orElseThrow(ResourceNotFound::new);
    }
}
