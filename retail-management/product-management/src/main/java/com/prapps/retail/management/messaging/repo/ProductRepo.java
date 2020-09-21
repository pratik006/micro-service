package com.prapps.retail.management.messaging.repo;

import com.prapps.retail.management.messaging.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long> { }
