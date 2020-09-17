package com.prapps.retail.management.product.entities;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String category;
    private String description;
}
