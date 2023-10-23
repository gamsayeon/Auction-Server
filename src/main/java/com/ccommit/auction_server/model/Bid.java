package com.ccommit.auction_server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "bid")
@Getter
@Setter
@Table(name = "bid")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private Long bidId;

    @Column(name = "buyer_id")
    private Long buyerId;

    @Column(name = "productId")
    private Long productId;

    @Column(name = "bid_time")
    private LocalDateTime bidTime;

    @Column(name = "price")
    private int price;

}