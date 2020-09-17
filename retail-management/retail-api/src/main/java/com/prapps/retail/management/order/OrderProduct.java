package com.prapps.retail.management.order;

import com.prapps.retail.management.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct {
    private Long id;
    private Long productId;
    private Long orderId;
    private Integer quantity;
    private Product product;
}
