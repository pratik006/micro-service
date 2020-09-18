package com.prapps.retail.management.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ProductControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String productServiceUrl;

    @BeforeEach
    public void setUp() {
        this.productServiceUrl = "http://localhost:" + port + "/product";
    }

    @Test
    public void getAliveMessage() {
        assertEquals("alive", this.restTemplate.getForEntity(productServiceUrl, String.class).getBody());
    }

    @Test
    public void getProduct() {
        String productTestCat = "test_category";
        String productTestDesc = "test desc";
        Product newProduct = new Product(null, productTestCat, productTestDesc);
        Product savedProduct = this.restTemplate.postForEntity(UriComponentsBuilder.fromHttpUrl(productServiceUrl).toUriString(), newProduct, Product.class).getBody();

        assert savedProduct != null;
        String uri = UriComponentsBuilder.fromHttpUrl(productServiceUrl).path("/"+savedProduct.getId()).build().toUriString();
        Product product = this.restTemplate.getForEntity(uri, Product.class).getBody();
        assertNotNull(product);
        assertEquals(savedProduct.getId(), product.getId());
        assertEquals(productTestCat, product.getCategory());
        assertEquals(productTestDesc, product.getDescription());
    }
}
