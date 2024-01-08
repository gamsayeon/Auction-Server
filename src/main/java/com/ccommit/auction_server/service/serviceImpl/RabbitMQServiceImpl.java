package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.exception.AddFailedException;
import com.ccommit.auction_server.exception.InputMismatchException;
import com.ccommit.auction_server.model.Bid;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.projection.UserProjection;
import com.ccommit.auction_server.repository.BidRepository;
import com.ccommit.auction_server.repository.ProductRepository;
import com.ccommit.auction_server.repository.UserRepository;
import com.ccommit.auction_server.service.EmailService;
import com.ccommit.auction_server.service.MQService;
import com.ccommit.auction_server.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

/***
 * 생성자 주입을 자동으로 처리 함
 */

@Profile({"dev", "test", "performance"})
@RequiredArgsConstructor
@Service
public class RabbitMQServiceImpl implements MQService {
    private final BidPriceValidServiceImpl bidPriceValidService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;

    private final PaymentService tossPaymentService;
    private static final Logger logger = LogManager.getLogger(RabbitMQServiceImpl.class);

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Override
    public void enqueueMassage(Bid bid) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //LocalDateTime을 JSON으로 변환하거나 JSON에서 해당 클래스로 역직렬화하기 위해 모듈을 등록하는 코드
            objectMapper.registerModule(new JavaTimeModule());
            String jsonStr = objectMapper.writeValueAsString(bid);
            rabbitTemplate.convertAndSend(exchangeName, routingKey, jsonStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void dequeueMassage(String jsonStr, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JavaTimeModule());
            Bid deserializedBid = objectMapper.readValue(jsonStr, Bid.class);
            bidPriceValidService.validBidPrice(deserializedBid.getProductId(), deserializedBid.getPrice());

            Bid resultBid = bidRepository.save(deserializedBid);
            Product product = productRepository.findByProductId(resultBid.getProductId());
            if (resultBid == null) {
                logger.warn("입찰이 되지 않았습니다.");
                throw new AddFailedException("BID_ADD_FAILED", deserializedBid);
            } else if (resultBid.getPrice() == product.getHighestPrice()) {
                product.setProductStatus(ProductStatus.AUCTION_END);
                productRepository.save(product);
                tossPaymentService.createPayment(resultBid.getPrice(), product.getProductName(), resultBid.getProductId());
            } else {
                logger.info("정상적으로 입찰 되었습니다.");
                UserProjection recipientEmail = userRepository.findUserProjectionById(resultBid.getBuyerId());
                emailService.notifyAuction(recipientEmail.getEmail(), "경매 입찰", product.getProductName() + "경매에 입찰하였습니다.");
                recipientEmail = userRepository.findUserProjectionById(product.getSaleId());
                emailService.notifyAuction(recipientEmail.getEmail(), "경매 입찰", product.getProductName() + "경매에 입찰하였습니다.");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InputMismatchException e) {
            //유효성 검사 실패로 인한 exception 발생시 큐에 메세지가 남아있어 해당 메세지 재처리안함(flase)
            channel.basicReject(tag, false);
        }
    }


}