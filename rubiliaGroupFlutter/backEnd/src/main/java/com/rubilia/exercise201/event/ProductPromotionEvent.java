package com.rubilia.exercise201.event;

import com.rubilia.exercise201.entity.Product;
import org.springframework.context.ApplicationEvent;

public class ProductPromotionEvent extends ApplicationEvent {

    private final Product product;

    public ProductPromotionEvent(Object source, Product product) {
        super(source);
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }
}
