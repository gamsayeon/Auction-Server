package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Payment findByOrderId(String orderId);

    Payment findByPaymentId(String paymentId);

    List<Payment> findByUserId(Long userId);
}
