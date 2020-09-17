package com.prapps.retail.management.order.entities;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long customerId;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "orderId")
    private Set<OrderProductEntity> orderProductEntities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Set<OrderProductEntity> getOrderProductEntities() {
        if (orderProductEntities == null) {
            orderProductEntities = new HashSet<>();
        }
        return orderProductEntities;
    }



    public void setOrderProductEntities(Set<OrderProductEntity> orderProductEntities) {
        this.orderProductEntities = orderProductEntities;
    }
}
