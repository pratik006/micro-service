package com.prapps.retail.management.order.feign;

import com.prapps.retail.management.customer.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(url = "${customerApp.url}", name = "customerServiceClient")
public interface CustomerServiceClient {
    @GetMapping("/{customerId}")
    Optional<Customer> findCustomer(@PathVariable("customerId") Long customerId);
}
