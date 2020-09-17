package com.prapps.retail.management.order.mapper;

import com.prapps.retail.management.order.Order;
import com.prapps.retail.management.order.entities.OrderEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private OrderProductMapper orderProductMapper;

    public OrderMapper(OrderProductMapper orderProductMapper) {
        this.orderProductMapper = orderProductMapper;
    }

    public Order map(OrderEntity entity) {
        Order order = new Order();
        BeanUtils.copyProperties(entity, order);
        order.setOrderProducts(entity.getOrderProductEntities().stream()
                .map(orderProductEntity -> orderProductMapper.map(orderProductEntity))
                .collect(Collectors.toSet()));
        return order;
    }

    public OrderEntity map(Order order) {
        OrderEntity entity = new OrderEntity();
        BeanUtils.copyProperties(order, entity);
        entity.setOrderProductEntities(order.getOrderProducts().stream()
                .map(orderProduct -> orderProductMapper.map(orderProduct))
                .collect(Collectors.toSet()));
        return entity;
    }
}
