package com.prapps.retail.management.messaging.mapper;

import com.prapps.retail.management.messaging.entities.ProductEntity;
import com.prapps.retail.management.product.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product map(ProductEntity entity) {
        Product product = new Product();
        BeanUtils.copyProperties(entity, product);
        return product;
    }

    public ProductEntity map(Product product) {
        ProductEntity entity = new ProductEntity();
        BeanUtils.copyProperties(product, entity);
        return entity;
    }
}
