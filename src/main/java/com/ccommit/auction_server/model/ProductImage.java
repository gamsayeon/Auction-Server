package com.ccommit.auction_server.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "product_image")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_image")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "image_path")
    private String imagePath;
}