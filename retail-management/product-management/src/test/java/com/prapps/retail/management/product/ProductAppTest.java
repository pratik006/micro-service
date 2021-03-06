/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.prapps.retail.management.product;

import com.prapps.retail.management.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ProductAppTest {
    @Autowired
    ProductService productService;

    @Test
    public void testCreateProduct() {
        String productTestCat = "test_category";
        String productTestDesc = "test desc";
        Product product = new Product(null, productTestCat, productTestDesc);
        Product savedProduct = productService.create(product);
        Product retrievedProduct = productService.findById(savedProduct.getId())
                .orElseThrow(AssertionError::new);
        assertEquals(productTestCat, retrievedProduct.getCategory());
        assertEquals(productTestDesc, retrievedProduct.getDescription());
    }
}
