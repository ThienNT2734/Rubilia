package com.rubilia.exercise201.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product_display_info", 
       uniqueConstraints = @UniqueConstraint(name = "uk_product_display", columnNames = {"product_id", "display_area"}))
@Getter
@Setter
public class ProductDisplayInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "display_area", nullable = false)
    private String displayArea;

    @Column(name = "sales_count")
    private BigDecimal salesCount;

    @Column(name = "rating")
    private BigDecimal rating;
}