package com.ccommit.auction_server.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "bid_min_price")
    private int bidMinPrice;
}