package com.prapps.retail.management.order.service;

import com.prapps.retail.management.order.Order;
import com.prapps.retail.management.order.entities.OrderEntity;
import com.prapps.retail.management.order.feign.CustomerServiceClient;
import com.prapps.retail.management.order.feign.ProductServiceClient;
import com.prapps.retail.management.order.mapper.OrderMapper;
import com.prapps.retail.management.order.repo.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private OrderRepo orderRepo;
    private OrderMapper orderMapper;
    private ProductServiceClient productServiceClient;
    private CustomerServiceClient customerServiceClient;

    @Autowired
    public OrderService(OrderRepo orderRepo, OrderMapper orderMapper, ProductServiceClient productServiceClient, CustomerServiceClient customerServiceClient) {
        this.orderRepo = orderRepo;
        this.orderMapper = orderMapper;
        this.productServiceClient = productServiceClient;
        this.customerServiceClient = customerServiceClient;
    }

    public Order create(Order order) {
        OrderEntity entity = orderMapper.map(order);
        OrderEntity savedEntity = orderRepo.save(entity);
        return orderMapper.map(savedEntity);
    }

    public Optional<Order> findById(Long id) {
        return orderRepo.findById(id)
                .map(orderEntity -> orderMapper.map(orderEntity))
                .map(order -> {
                    customerServiceClient.findCustomer(order.getCustomerId())
                            .ifPresent(order::setCustomer);
                    order.getOrderProducts().forEach(orderProduct -> productServiceClient.findProduct(orderProduct.getProductId())
                            .ifPresent(orderProduct::setProduct));

                    return order;
                });
    }

    public List<Order> findOrderByCustomer(Long customerId) {
        return orderRepo.findByCustomerId(customerId).stream()
                .map(entity -> orderMapper.map(entity))
                .collect(Collectors.toList());
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll().stream().map(orderEntity -> orderMapper.map(orderEntity))
                .peek(order -> {
                    customerServiceClient.findCustomer(order.getCustomerId()).ifPresent(order::setCustomer);
                    order.getOrderProducts().forEach(orderProduct -> productServiceClient.findProduct(orderProduct.getProductId())
                            .ifPresent(orderProduct::setProduct));
                }).collect(Collectors.toList());
    }
}
