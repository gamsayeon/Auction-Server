package com.ccommit.auction_server.service;

public interface EmailService {
    void sendTokenToUser(String token, String email);

    void notifyAuction(String recipientEmail, String subject, String message);
}
