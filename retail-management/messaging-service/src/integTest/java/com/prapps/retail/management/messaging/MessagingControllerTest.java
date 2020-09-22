package com.prapps.retail.management.messaging;

import com.prapps.retail.management.customer.Customer;
import com.prapps.retail.management.messaging.dto.MessagingRequest;
import com.prapps.retail.management.order.Order;
import com.prapps.retail.management.order.OrderProduct;
import com.prapps.retail.management.product.Product;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MessagingApp.class)
@ActiveProfiles("test")
public class MessagingControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingControllerTest.class);
    private static final String TEST_TOPIC_NAME = "placedOrders";

    @LocalServerPort
    private int port;

    private String messagingServiceUrl;
    private Optional<Order> receivedMessage = Optional.empty();
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

        this.messagingServiceUrl = "http://"+minikubeIp+":32005/messaging";
    }

    @Test
    public void getAliveMessage() {
        assertEquals("alive", this.restTemplate.getForEntity(messagingServiceUrl, String.class).getBody());
    }

    //@Test
    public void testSendMessage() {
        MessagingRequest messagingRequest = new MessagingRequest();
        Order order = new Order();
        order.setId(101L);
        order.setCustomerId(201L);
        order.setCustomer(new Customer(201L, "Pratik", "Sengupta"));
        order.setOrderProducts(Collections.singleton(
                new OrderProduct(1L, 123L, 101L, 3,
                new Product(5L, "test_category", "test desc pass"))));
        messagingRequest.setPayload(order);
        Void voidResp = this.restTemplate.postForEntity(messagingServiceUrl + "/send/"+TEST_TOPIC_NAME, messagingRequest, Void.class).getBody();
        Assumptions.assumeTrue(voidResp != null);
        try {
            synchronized (this) {
                this.wait(10000);
            }
            assertNotNull(receivedMessage);
            assertTrue(receivedMessage.isPresent());
            assertEquals(201L, receivedMessage.get().getCustomerId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void testSendMessageFail() {
        String topicName = "test";
        MessagingRequest messagingRequest = new MessagingRequest();
        Order order = new Order();
        order.setId(101L);
        order.setCustomerId(201L);
        order.setCustomer(new Customer(201L, "Pratik", "Sengupta"));
        order.setOrderProducts(Collections.singleton(new OrderProduct(1L, 123L, 101L, 3, new Product(5L, "test_category", "test desc"))));
        messagingRequest.setPayload(order);
        this.restTemplate.postForEntity(messagingServiceUrl + "/send/"+topicName, messagingRequest, Void.class);

        try {
            synchronized (this) {
                this.wait(5000);
            }
            assertFalse(receivedMessage.isPresent());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*@KafkaListener(topics = TEST_TOPIC_NAME, containerGroup = "integTestGrp")
    public void listen2(@Payload Order order) {
        LOGGER.info("Received: " + order);
        this.receivedMessage = Optional.of(order);
        synchronized (this) {
            this.notifyAll();
        }
    }*/

}
