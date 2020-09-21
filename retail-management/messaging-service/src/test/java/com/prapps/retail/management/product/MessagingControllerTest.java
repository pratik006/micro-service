package com.prapps.retail.management.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MessagingControllerTest {
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
}
