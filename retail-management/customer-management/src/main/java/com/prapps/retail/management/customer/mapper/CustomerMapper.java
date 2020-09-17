package com.prapps.retail.management.customer.mapper;

import com.prapps.retail.management.customer.Customer;
import com.prapps.retail.management.customer.entities.CustomerEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public Customer map(CustomerEntity entity) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(entity, customer);
        return customer;
    }

    public CustomerEntity map(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        BeanUtils.copyProperties(customer, entity);
        return entity;
    }
}
