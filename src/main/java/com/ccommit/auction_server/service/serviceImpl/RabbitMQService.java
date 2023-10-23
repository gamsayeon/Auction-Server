package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.model.Bid;
import com.ccommit.auction_server.projection.UserProjection;
import com.ccommit.auction_server.repository.BidRepository;
import com.ccommit.auction_server.repository.UserRepository;
import com.ccommit.auction_server.service.MQService;
import com.ccommit.auction_server.exception.AddFailedException;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/***
 * 생성자 주입을 자동으로 처리 함
 */

@Profile({"dev"})
@RequiredArgsConstructor
@Service
public class RabbitMQService implements MQService {
    private final BidPriceValidServiceImpl bidPriceValidService;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;
    private final RabbitTemplate rabbitTemplate;
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;
    private static final Logger logger = LogManager.getLogger(RabbitMQService.class);

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;


    @Override
    public void enqueueMassage(Bid bid) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JavaTimeModule());
            String jsonStr = objectMapper.writeValueAsString(bid);
            rabbitTemplate.convertAndSend(exchangeName, routingKey, jsonStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void dequeueMassage(String jsonStr) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JavaTimeModule());
            Bid deserializedBid = objectMapper.readValue(jsonStr, Bid.class);
            bidPriceValidService.validBidPrice(deserializedBid.getProductId(), deserializedBid.getPrice());

            Bid resultBid = bidRepository.save(deserializedBid);
            if (resultBid == null) {
                logger.warn("입찰이 되지 않았습니다.");
                throw new AddFailedException("BID_ADD_FAILED", deserializedBid);
            } else {
                logger.info("정상적으로 입찰 되었습니다.");
                UserProjection recipientEmail = userRepository.findUserProjectionById(resultBid.getBuyerId());
                Product product = productRepository.findByProductId(resultBid.getProductId());
                emailService.notifyAuction(recipientEmail.getEmail(), "경매 입찰", product.getProductName() + "경매에 입찰하였습니다.");
                recipientEmail = userRepository.findUserProjectionById(product.getSaleId());
                emailService.notifyAuction(recipientEmail.getEmail(), "경매 입찰", product.getProductName() + "경매에 입찰하였습니다.");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


}