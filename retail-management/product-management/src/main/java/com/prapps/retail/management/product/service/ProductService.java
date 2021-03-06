package com.prapps.retail.management.product.service;

import com.prapps.retail.management.product.Product;
import com.prapps.retail.management.product.entities.ProductEntity;
import com.prapps.retail.management.product.mapper.ProductMapper;
import com.prapps.retail.management.product.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private ProductRepo productRepo;
    private ProductMapper orderMapper;

    @Autowired
    public ProductService(ProductRepo productRepo, ProductMapper orderMapper) {
        this.productRepo = productRepo;
        this.orderMapper = orderMapper;
    }

    public Product create(Product order) {
        ProductEntity entity = orderMapper.map(order);
        ProductEntity savedEntity = productRepo.save(entity);
        return orderMapper.map(savedEntity);
    }

    public Optional<Product> findById(Long id) {
        return productRepo.findById(id).map(entity1 -> orderMapper.map(entity1));
    }
}
