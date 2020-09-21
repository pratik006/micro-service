package com.prapps.retail.management.order;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class OrderAppIntegTest {
    private static final Logger LOG = LoggerFactory.getLogger(OrderAppIntegTest.class);
    private static final String ALL_ORDER_URL = "/order/all";
    private TestRestTemplate restTemplate;
    private String minikubeIp;

    @BeforeEach
    public void setUp() {
        restTemplate = new TestRestTemplate();
        try {
            Process process = Runtime.getRuntime().exec("minikube ip");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.destroy();
            minikubeIp = line.trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAllOrders() {
        Order[] orders = restTemplate.getForEntity("http://"+minikubeIp+":32003"+ALL_ORDER_URL, Order[].class).getBody();
        LOG.debug("orders: " + Arrays.toString(orders));
        Assertions.assertTrue(orders.length > 0);
    }

}
