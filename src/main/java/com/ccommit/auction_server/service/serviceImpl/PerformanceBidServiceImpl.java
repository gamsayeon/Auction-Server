package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.model.Bid;
import com.ccommit.auction_server.model.Category;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.repository.BidRepository;
import com.ccommit.auction_server.repository.CategoryRepository;
import com.ccommit.auction_server.repository.ProductRepository;
import com.ccommit.auction_server.service.MQService;
import com.ccommit.auction_server.service.PerformanceBidService;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PerformanceBidServiceImpl implements PerformanceBidService {
    private final BidRepository bidRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MQService rabbitMQService;
    private Faker faker;

    @Override
    public void performanceRegisterBid() {
        //성능테스트 용 입찰 정보 10만개
        for (int i = 0; i < 100000; i++) {
            Long generatedBuyerId = Double.valueOf(Math.random() * 20 + 1).longValue();
            Long generatedProductId = Double.valueOf(Math.random() * 100000 + 1).longValue();
            Bid bid = bidRepository.findTopByProductIdOrderByPriceDesc(generatedProductId);
            Product product = productRepository.findByProductId(generatedProductId);
            Optional<Category> category = categoryRepository.findByCategoryId(product.getCategoryId());
            int generatedStartPrice = (int) (category.get().getBidMinPrice() * Math.random() * 10 + 1);
            if (bid == null) {
                generatedStartPrice = generatedStartPrice + product.getStartPrice();
            } else {
                generatedStartPrice = generatedStartPrice + bid.getPrice();
            }

            Bid registerBid = Bid.builder()
                    .buyerId(generatedBuyerId)
                    .productId(generatedProductId)
                    .price(generatedStartPrice)
                    .bidTime(LocalDateTime.now())
                    .build();

            bidRepository.save(registerBid);
        }
    }

    public void rabbitMQEnqueueBid() {
        for (int i = 0; i < 20; i++) {
            faker = new Faker();
            Long generatedBuyerId = Double.valueOf(Math.random() * 20 + 1).longValue();
            Long generatedProductId = Double.valueOf(Math.random() * 100000 + 1).longValue();
            Bid bid = bidRepository.findTopByProductIdOrderByPriceDesc(generatedProductId);
            Product product = productRepository.findByProductId(generatedProductId);
            Optional<Category> category = categoryRepository.findByCategoryId(product.getCategoryId());
            int generatedStartPrice = (int) (category.get().getBidMinPrice() * Math.random() * 10 + 1);
            if (bid == null) {
                generatedStartPrice = generatedStartPrice + product.getStartPrice();
            } else {
                generatedStartPrice = generatedStartPrice + bid.getPrice();
            }

            Bid registerBid = Bid.builder()
                    .buyerId(generatedBuyerId)
                    .productId(generatedProductId)
                    .bidTime(LocalDateTime.now())
                    .price(generatedStartPrice)
                    .build();

            Instant startInstant = Instant.now();

            rabbitMQService.enqueueMassage(registerBid);

            Instant endInstant = Instant.now();
            Duration duration = Duration.between(startInstant, endInstant);
            long elapsedTime = duration.toMillis();

            System.out.println("경과 시간: " + elapsedTime + "ms");
        }
    }

    public Integer selectTopBidPrice(Long productId) {
        Bid bid = bidRepository.findTopByProductIdOrderByPriceDesc(productId);
        int price;
        if (bid == null) {
            Product product = productRepository.findByProductId(productId);
            price = product.getStartPrice();
        } else {
            price = bid.getPrice();
        }
        return price;
    }
}
