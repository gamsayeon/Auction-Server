package com.ccommit.auction_server.model;

import com.ccommit.auction_server.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "product")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "product_register_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime productRegisterTime;

    @Column(name = "start_price")
    private int startPrice;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime endTime;

    @Column(name = "highest_price")
    private int highestPrice;

    @Column(name = "product_status")
    private ProductStatus productStatus;
}