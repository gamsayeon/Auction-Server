package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.enums.PaymentStatus;
import com.ccommit.auction_server.exception.AddFailedException;
import com.ccommit.auction_server.exception.NetworkConnectionException;
import com.ccommit.auction_server.exception.PaymentFailedException;
import com.ccommit.auction_server.model.Payment;
import com.ccommit.auction_server.model.toss.*;
import com.ccommit.auction_server.projection.UserProjection;
import com.ccommit.auction_server.repository.BidRepository;
import com.ccommit.auction_server.repository.PaymentRepository;
import com.ccommit.auction_server.repository.UserRepository;
import com.ccommit.auction_server.service.EmailService;
import com.ccommit.auction_server.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TossPaymentServiceImpl implements PaymentService {
    @Value("${toss.api.key}")
    private String apiKey;

    @Value("${toss.api.url}")
    private String tossURL;

    @Value("${payment.ret.url}")
    private String retURL;

    @Value("${payment.ret.cancel.url}")
    private String retCancelURL;

    final Integer RESPONSE_SUCCESS_CODE = 0;
    private final EmailService emailService;
    private final BidRepository bidRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(TossPaymentServiceImpl.class);

    @Override
    @Transactional
    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 1000))
    public void createPayment(int price, String productName, Long productId) {
        StringBuilder responseBody = new StringBuilder();
        String uuid = UUID.randomUUID().toString();
        String orderNo = uuid.replaceAll("-", "").substring(0, 10);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            URL url = new URL(tossURL + "/payments");
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                    .orderNo(orderNo)
                    .amount(price)
                    .amountTaxFree(0)
                    .productDesc(productName)
                    .apiKey(apiKey)
                    .retUrl(retURL + orderNo)
                    .retCancelUrl(retCancelURL + orderNo)
                    .autoExecute(false)
                    .build();

            String jsonStr = objectMapper.writeValueAsString(paymentCreateRequest);
            responseBody = this.getOutputConnection(connection, jsonStr, responseBody);

            Map<String, Object> successMap = objectMapper.readValue(responseBody.toString(), Map.class);

            // "code" 값을 사용하여 성공(0)과 실패(-1)를 분리
            int code = (int) successMap.get("code");
            PaymentCreateResponse paymentCreateResponse = objectMapper.readValue(responseBody.toString(), PaymentCreateResponse.class);

            if (code == RESPONSE_SUCCESS_CODE) {    // 성공 응답 처리
                Long buyerId = bidRepository.findTopByProductIdOrderByPriceDesc(productId).getBuyerId();
                UserProjection email = userRepository.findUserProjectionById(buyerId);
                emailService.notifyAuction(email.getEmail(), "결제 요청", paymentCreateResponse.getCheckoutPage());
                Payment payment = Payment.builder()
                        .paymentId(paymentCreateResponse.getPayToken())
                        .orderId(orderNo)
                        .userId(buyerId)
                        .productId(productId)
                        .paymentStatus(PaymentStatus.PAY_STANDBY)
                        .paymentAmount(paymentCreateRequest.getAmount())
                        .build();
                Payment resultPayment = paymentRepository.save(payment);
                if (resultPayment != null) {
                    logger.warn("Payment을 추가 하지 못했습니다.");
                    throw new AddFailedException("PAYMENT_ADD_FAILED", resultPayment);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            responseBody.append(e);
            logger.warn("네트워크가 불안정합니다. 재시도 해주세요.");
            throw new NetworkConnectionException("PAYMENT_NETWORK_CONNECTION_ERROR");
        }
    }

    @Override
    public void closePayment(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        payment.setPaymentStatus(PaymentStatus.PAY_CANCEL);
        Payment resultPayment = paymentRepository.save(payment);
        if (resultPayment != null) {
            logger.warn("Payment을 추가 하지 못했습니다.");
            throw new AddFailedException("PAYMENT_ADD_FAILED", resultPayment);
        }
    }

    public StringBuilder getOutputConnection(URLConnection connection, String jsonStr, StringBuilder responseBody) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());

            bos.write(jsonStr.getBytes());
            bos.flush();
            bos.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line = null;
            while ((line = br.readLine()) != null) {
                responseBody.append(line);
            }
            br.close();
        } catch (IOException e) {
            responseBody.append(e);
        }
        return responseBody;
    }

    @Override
    public PaymentApproveResponse approvePayment(String orderNo) {
        StringBuilder responseBody = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        PaymentApproveResponse paymentApproveResponse = null;
        try {
            URL url = new URL(tossURL + "/execute");
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            Payment payment = paymentRepository.findByOrderId(orderNo);

            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .apiKey(apiKey)
                    .payToken(payment.getPaymentId())
                    .build();

            String jsonStr = objectMapper.writeValueAsString(paymentRequest);
            responseBody = this.getOutputConnection(connection, jsonStr, responseBody);

            paymentApproveResponse = objectMapper.readValue(responseBody.toString(), PaymentApproveResponse.class);
            if (payment.getPaymentStatus() != PaymentStatus.PAY_STANDBY) {
                logger.warn("결제 상태가 결제대기 상태가 아닙니다.");
                throw new PaymentFailedException("PAYMENT_STATUS_FAILED", payment.getPaymentStatus());
            } else if (paymentApproveResponse.getCode() == RESPONSE_SUCCESS_CODE &&
                    payment.getPaymentAmount() == paymentApproveResponse.getAmount()) {
                payment.setPaymentDate(LocalDateTime.now());
                payment.setPayMethod(paymentApproveResponse.getPayMethod());
                payment.setPaymentStatus(PaymentStatus.PAY_COMPLETE);
                Payment resultPayment = paymentRepository.save(payment);
                if (resultPayment != null) {
                    logger.warn("Payment을 추가 하지 못했습니다.");
                    throw new AddFailedException("PAYMENT_ADD_FAILED", resultPayment);
                }
            } else {
                this.closePayment(orderNo);
                logger.warn("요청한 금액과 결제금액이 다릅니다.");
                throw new PaymentFailedException("PAYMENT_AMOUNT_NOT_MATCH", payment.getPaymentAmount());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            responseBody.append(e);
            logger.warn("네트워크가 불안정합니다. 재시도 해주세요.");
            throw new NetworkConnectionException("PAYMENT_NETWORK_CONNECTION_ERROR");
        }
        return paymentApproveResponse;
    }

    @Override
    public List<PaymentSelectResponse> selectPayment(Long userId) {
        StringBuilder responseBody = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        List<PaymentSelectResponse> paymentSelectResponses = null;
        List<Payment> payments = paymentRepository.findByUserId(userId);
        for (Payment payment : payments) {
            try {
                URL url = new URL(tossURL + "/status");
                URLConnection connection = url.openConnection();
                connection.addRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                PaymentRequest paymentRequest = PaymentRequest.builder()
                        .apiKey(apiKey)
                        .payToken(payment.getPaymentId())
                        .build();

                String jsonStr = objectMapper.writeValueAsString(paymentRequest);
                responseBody = this.getOutputConnection(connection, jsonStr, responseBody);

                PaymentSelectResponse paymentSelectResponse = objectMapper.readValue(responseBody.toString(), PaymentSelectResponse.class);
                paymentSelectResponses.add(paymentSelectResponse);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                responseBody.append(e);
                logger.warn("네트워크가 불안정합니다. 재시도 해주세요.");
                throw new NetworkConnectionException("PAYMENT_NETWORK_CONNECTION_ERROR");
            }
        }
        return paymentSelectResponses;
    }


    @Override
    public PaymentRefundsResponse refundsPayment(String paymentId) {
        StringBuilder responseBody = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Payment payment = paymentRepository.findByPaymentId(paymentId);
        PaymentRefundsResponse paymentRefundsResponse = null;
        try {
            URL url = new URL(tossURL + "/refunds");
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .apiKey(apiKey)
                    .payToken(payment.getPaymentId())
                    .build();

            String jsonStr = objectMapper.writeValueAsString(paymentRequest);
            responseBody = this.getOutputConnection(connection, jsonStr, responseBody);

            paymentRefundsResponse = objectMapper.readValue(responseBody.toString(), PaymentRefundsResponse.class);
            if (paymentRefundsResponse.getCode() == RESPONSE_SUCCESS_CODE) {
                payment.setPaymentDate(LocalDateTime.now());
                payment.setPaymentStatus(PaymentStatus.REFUND_SUCCESS);
                Payment resultPayment = paymentRepository.save(payment);
                if (resultPayment != null) {
                    logger.warn("Payment을 추가 하지 못했습니다.");
                    throw new AddFailedException("PAYMENT_ADD_FAILED", resultPayment);
                }
            } else {
                logger.warn("금액 불일치로 인해 환불에 실패 했습니다.");
                throw new PaymentFailedException("PAYMENT_REFUNDS_FAILED", payment.getPaymentAmount());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            responseBody.append(e);
            logger.warn("네트워크가 불안정합니다. 재시도 해주세요.");
            throw new NetworkConnectionException("PAYMENT_NETWORK_CONNECTION_ERROR");
        }
        return paymentRefundsResponse;
    }

}
