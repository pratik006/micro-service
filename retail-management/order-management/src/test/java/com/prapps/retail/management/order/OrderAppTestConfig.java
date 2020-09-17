package com.prapps.retail.management.order;

import com.prapps.retail.management.customer.Customer;
import com.prapps.retail.management.order.feign.CustomerServiceClient;
import com.prapps.retail.management.order.feign.ProductServiceClient;
import com.prapps.retail.management.product.Product;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

@SpringBootApplication
@Profile("test")
public class OrderAppTestConfig {
    @Bean
    ProductServiceClient mockProductServiceClient() {
        return productId -> Optional.of(new Product(1L, "test_category", "test desc"));
    }

    @Bean
    CustomerServiceClient mockCustomerServiceClient() {
        return customerId -> Optional.of(new Customer(1L, "Pratik", "Sengupta"));
    }
}
