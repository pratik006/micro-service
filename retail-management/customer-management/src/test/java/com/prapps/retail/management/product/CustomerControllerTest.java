package com.prapps.retail.management.product;

import com.prapps.retail.management.customer.Customer;
import com.prapps.retail.management.customer.CustomerApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(classes = CustomerApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String productServiceUrl;

    @BeforeEach
    public void setUp() {
        this.productServiceUrl = "http://localhost:" + port + "/customer";
    }

    @Test
    public void getAliveMessage() {
        assertEquals("alive", this.restTemplate.getForEntity(productServiceUrl, String.class).getBody());
    }

    @Test
    public void getProduct() {
        String fName = "Pratik";
        String lName = "Sengupta";
        Customer newProduct = new Customer(null, fName, lName);
        Customer savedProduct = this.restTemplate.postForEntity(UriComponentsBuilder.fromHttpUrl(productServiceUrl).toUriString(), newProduct, Customer.class).getBody();

        assert savedProduct != null;
        String uri = UriComponentsBuilder.fromHttpUrl(productServiceUrl).path("/"+savedProduct.getId()).build().toUriString();
        Customer customer = this.restTemplate.getForEntity(uri, Customer.class).getBody();
        assertNotNull(customer);
        assertEquals(savedProduct.getId(), customer.getId());
        assertEquals(fName, customer.getFirstName());
        assertEquals(lName, customer.getLastName());
    }
}
