package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.model.Bid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BidRepository Unit 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BidRepositoryTest {
    @Autowired
    private BidRepository bidRepository;
    private Long TEST_BUYER_ID = 500L;
    private Long TEST_PRODUCT_ID = 500L;
    private int BID_COUNT = 2;
    private int TEST_MAX_PRICE = 10000 * BID_COUNT;

    @BeforeEach
    public void generateTestBid() {
        //given
        for (int i = 1; i <= BID_COUNT; i++) {
            Bid bid = Bid.builder()
                    .buyerId(TEST_BUYER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .bidTime(LocalDateTime.now())
                    .price(10000 * i)
                    .build();

            bidRepository.save(bid);
        }
    }

    @Test
    @DisplayName("상품의 입찰 최댓값 조회")
    void findMaxPriceByProductId() {
        //when
        Integer findMaxBid = bidRepository.findMaxPriceByProductId(TEST_BUYER_ID);

        //then
        assertNotNull(findMaxBid);
        assertEquals(TEST_MAX_PRICE, findMaxBid);
    }

    @Test
    @DisplayName("상품의 입찰 갯수 조회")
    void countByProductId() {
        //when
        Long findCountBid = bidRepository.countByProductId(TEST_PRODUCT_ID);

        //then
        assertNotNull(findCountBid);
        assertEquals(BID_COUNT, findCountBid);
    }

    @Test
    @DisplayName("구매자 식별자로 입찰 조회")
    void findByBuyerId() {
        //when
        List<Bid> findBids = bidRepository.findByBuyerId(TEST_BUYER_ID);

        //then
        assertFalse(findBids.isEmpty());
        for (Bid bid : findBids) {
            assertEquals(TEST_BUYER_ID, bid.getBuyerId());
        }
    }

    @Test
    @DisplayName("구매자 식별자와 상품 식별자로 입찰 조회")
    void findByBuyerIdAndProductId() {
        //when
        List<Bid> findBids = bidRepository.findByBuyerIdAndProductId(TEST_BUYER_ID, TEST_PRODUCT_ID);

        //then
        assertFalse(findBids.isEmpty());
        for (Bid bid : findBids) {
            assertEquals(TEST_BUYER_ID, bid.getBuyerId());
            assertEquals(TEST_PRODUCT_ID, bid.getProductId());
        }
    }

    @Test
    @DisplayName("판매자의 상품 식별자로 입찰 조회")
    void findByProductId() {
        //when
        List<Bid> findBids = bidRepository.findByProductId(TEST_PRODUCT_ID);

        //then
        assertFalse(findBids.isEmpty());
        for (Bid bid : findBids) {
            assertEquals(TEST_PRODUCT_ID, bid.getProductId());
        }
    }
}