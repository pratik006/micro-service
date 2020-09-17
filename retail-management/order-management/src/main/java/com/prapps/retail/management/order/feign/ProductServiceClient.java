package com.prapps.retail.management.order.feign;

import com.prapps.retail.management.product.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(url = "${productApp.url}", name = "productServiceClient")
public interface ProductServiceClient {
    @GetMapping("/{productId}")
    Optional<Product> findProduct(@PathVariable("productId") Long productId);
}
