package com.prapps.retail.management.customer.service;

import com.prapps.retail.management.customer.Customer;
import com.prapps.retail.management.customer.repo.CustomerRepo;
import com.prapps.retail.management.customer.entities.CustomerEntity;
import com.prapps.retail.management.customer.mapper.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private CustomerRepo customerRepo;
    private CustomerMapper orderMapper;

    @Autowired
    public CustomerService(CustomerRepo productRepo, CustomerMapper orderMapper) {
        this.customerRepo = productRepo;
        this.orderMapper = orderMapper;
    }

    public Customer create(Customer order) {
        CustomerEntity entity = orderMapper.map(order);
        CustomerEntity savedEntity = customerRepo.save(entity);
        return orderMapper.map(savedEntity);
    }

    public Optional<Customer> findById(Long id) {
        return customerRepo.findById(id).map(entity1 -> orderMapper.map(entity1));
    }
}
