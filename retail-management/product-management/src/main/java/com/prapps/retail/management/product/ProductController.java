package com.prapps.retail.management.product;

import com.prapps.retail.management.exception.ResourceNotFound;
import com.prapps.retail.management.messaging.service.ProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService orderService) {
        this.productService = orderService;
    }

    @GetMapping
    public String alive() {
        return "alive";
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.create(product);
    }

    @GetMapping("/{id}")
    public Product findProductById(@PathVariable("id") Long productId) {
        return productService.findById(productId).orElseThrow(ResourceNotFound::new);
    }
}
