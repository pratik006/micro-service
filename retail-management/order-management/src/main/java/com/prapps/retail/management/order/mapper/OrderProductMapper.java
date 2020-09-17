package com.prapps.retail.management.order.mapper;

import com.prapps.retail.management.order.OrderProduct;
import com.prapps.retail.management.order.entities.OrderProductEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class OrderProductMapper {
    public OrderProduct map(OrderProductEntity orderProductEntity) {
        OrderProduct orderProduct = new OrderProduct();
        BeanUtils.copyProperties(orderProductEntity, orderProduct);
        orderProduct.setOrderId(orderProductEntity.getOrderId());
        return orderProduct;
    }

    public OrderProductEntity map(OrderProduct orderProduct) {
        OrderProductEntity orderProductEntity = new OrderProductEntity();
        BeanUtils.copyProperties(orderProduct, orderProductEntity);
        return orderProductEntity;
    }
}
