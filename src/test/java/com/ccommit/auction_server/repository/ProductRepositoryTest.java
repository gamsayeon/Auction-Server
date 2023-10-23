package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductRepository Unit 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;
    private Long TEST_SALE_ID = 500L;
    private Long TEST_CATEGORY_ID = 500L;
    private int PRODUCT_COUNT = 3;
    private Product savedProduct;
    private int DELETE_SUCCESS = 1;

    @BeforeEach
    public void generateTestProduct() {
        //given
        for (int i = 0; i < PRODUCT_COUNT; i++) {
            Product product = Product.builder()
                    .saleId(TEST_SALE_ID)
                    .productName("testProductName")
                    .categoryId(TEST_CATEGORY_ID)
                    .explanation("testExplanation")
                    .productRegisterTime(LocalDateTime.now())
                    .startPrice(1000)
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now())
                    .highestPrice(1000000)
                    .productStatus(ProductStatus.PRODUCT_REGISTRATION)
                    .build();

            savedProduct = productRepository.save(product);
        }
    }

    @Test
    @DisplayName("상품 식별자로 상품 조회")
    void findByProductId() {
        //when
        Product findProduct = productRepository.findByProductId(savedProduct.getProductId());

        //then
        assertNotNull(findProduct);
        assertEquals(savedProduct.getProductId(), findProduct.getProductId());
    }

    @Test
    @DisplayName("판매자 식별자로 상품 조회")
    void findBySaleId() {
        //when
        List<Product> findProducts = productRepository.findBySaleId(TEST_SALE_ID);

        //then
        assertNotNull(findProducts);
        for (Product findProduct : findProducts) {
            assertEquals(TEST_SALE_ID, findProduct.getSaleId());
        }
    }

    @Test
    @DisplayName("상품 식별자와 판매자 식별자로 상품 삭제")
    void deleteByProductId() {
        //when
        int deleteProduct = productRepository.deleteByProductId(savedProduct.getProductId());

        //then
        assertNull(productRepository.findByProductId(savedProduct.getProductId()));
        assertEquals(DELETE_SUCCESS, deleteProduct);
    }

    @Test
    @DisplayName("상품 상태를 기반으로 한 상품 조회")
    void findByProductStatus() {
        //when
        List<Product> findProducts = productRepository.findByProductStatus(ProductStatus.PRODUCT_REGISTRATION, ProductStatus.AUCTION_PROCEEDING);

        //then
        assertNotNull(findProducts);
        for (Product findProduct : findProducts) {
            assertEquals(ProductStatus.PRODUCT_REGISTRATION, findProduct.getProductStatus());
        }
    }

    @Test
    @DisplayName("상품 식별자로 유효한지")
    void existsByProductId() {
        //when
        boolean existsProduct = productRepository.existsByProductId(savedProduct.getProductId());

        //then
        assertTrue(existsProduct);
    }
}