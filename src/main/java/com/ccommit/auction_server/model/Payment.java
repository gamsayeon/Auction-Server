package com.ccommit.auction_server.model;

import com.ccommit.auction_server.enums.PayMethod;
import com.ccommit.auction_server.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
@Entity(name = "payment")
public class Payment {
    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "payment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime paymentDate;

    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "pay_method")
    private PayMethod payMethod;

    @Column(name = "payment_amount")
    private Integer paymentAmount;
}
