package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.model.Category;
import com.ccommit.auction_server.repository.BidRepository;
import com.ccommit.auction_server.repository.CategoryRepository;
import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DisplayName("BidPriceValidServiceImpl Unit 테스트")
@ExtendWith(MockitoExtension.class)
class BidPriceValidServiceImplTest {
    @InjectMocks
    private BidPriceValidServiceImpl bidPriceValidService;
    @Mock
    private BidRepository bidRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    private Category category;
    private Product product;
    private String TEST_CATEGORY_NAME = "testCategoryName";
    private int TEST_BID_MIN_PRICE = 1000;
    private Long TEST_PRODUCT_ID = 1L;
    private Long TEST_SALE_ID = 1L;
    private Long TEST_CATEGORY_ID = 1L;
    private Integer TEST_CURRENT_PRICE = 3000;
    private Integer TEST_PRICE = 5000;

    @BeforeEach
    public void generateTestBid() {
        category = Category.builder()
                .categoryId(1L)
                .categoryName(TEST_CATEGORY_NAME)
                .bidMinPrice(TEST_BID_MIN_PRICE)
                .build();

        product = Product.builder()
                .productId(TEST_PRODUCT_ID)
                .saleId(TEST_SALE_ID)
                .productName("test Product Name")
                .categoryId(TEST_CATEGORY_ID)
                .explanation("test Explanation")
                .productRegisterTime(LocalDateTime.now())
                .startPrice(1000)
                .startTime(LocalDateTime.now().plus(30, ChronoUnit.MINUTES))
                .endTime(LocalDateTime.now().plus(1, ChronoUnit.HOURS))
                .highestPrice(100000)
                .productStatus(ProductStatus.PRODUCT_REGISTRATION)
                .build();
    }

    @Test
    @DisplayName("입찰가 유효성 검사 성공 테스트")
    void validBidPrice() {
        //given
        when(productRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(product);
        when(categoryRepository.findByCategoryId(TEST_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(bidRepository.findMaxPriceByProductId(TEST_PRODUCT_ID)).thenReturn(TEST_CURRENT_PRICE);

        //when, then
        assertDoesNotThrow(() -> bidPriceValidService.validBidPrice(TEST_PRODUCT_ID, TEST_PRICE));
    }
}