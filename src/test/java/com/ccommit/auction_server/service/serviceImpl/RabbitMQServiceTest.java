package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.model.Bid;
import com.ccommit.auction_server.repository.BidRepository;
import com.ccommit.auction_server.repository.UserRepository;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.projection.UserProjection;
import com.ccommit.auction_server.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DisplayName("RabbitMQService Unit 테스트")
@ExtendWith(MockitoExtension.class)
class RabbitMQServiceTest {
    @InjectMocks
    private RabbitMQService rabbitMQService;
    @Mock
    private BidPriceValidServiceImpl bidPriceValidService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BidRepository bidRepository;
    @Mock
    private EmailServiceImpl emailService;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private ObjectMapper objectMapper;
    private Product product;
    private Bid bid;
    private Long TEST_PRODUCT_ID = 1L;
    private Long TEST_SALE_ID = 1L;
    private Long TEST_BID_ID = 1L;
    private Long TEST_BUYER_ID = 1L;
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @BeforeEach
    private void generatedTestRabbitMQ() {
        bid = Bid.builder()
                .bidId(TEST_BID_ID)
                .buyerId(TEST_BUYER_ID)
                .productId(TEST_PRODUCT_ID)
                .price(10000)
                .build();

        product = Product.builder()
                .productId(TEST_PRODUCT_ID)
                .productName("test product name")
                .build();
    }

    @Test
    @DisplayName("enqueueMassage 성공 테스트")
    void enqueueMassage() {
        //given
        ReflectionTestUtils.setField(rabbitMQService, "exchangeName", exchangeName);
        ReflectionTestUtils.setField(rabbitMQService, "routingKey", routingKey);

        //when, then
        assertDoesNotThrow(() -> rabbitMQService.enqueueMassage(bid));
    }

    @Test
    @DisplayName("dequeueMassage 성공 테스트")
    void dequeueMassage() throws JsonProcessingException {
        //given
        ObjectMapper objectMapper = new ObjectMapper();
        String testJsonStr = objectMapper.writeValueAsString(bid);
        when(bidRepository.save(any(Bid.class))).thenReturn(bid);
        when(productRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(product);
        UserProjection testRecipientEmail = () -> {
            return "test@example.com"; // 원하는 이메일 주소로 설정
        };
        when(userRepository.findUserProjectionById(TEST_SALE_ID)).thenReturn(testRecipientEmail);
        when(userRepository.findUserProjectionById(product.getSaleId())).thenReturn(testRecipientEmail);

        //when, then
        assertDoesNotThrow(() -> rabbitMQService.dequeueMassage(testJsonStr));
    }
}