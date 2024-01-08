package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.elasticsearchRepository.repositoryImpl.ProductSearchRepositoryImplElk;
import com.ccommit.auction_server.enums.ProductSortOrder;
import com.ccommit.auction_server.model.ELK.DocumentProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.*;

@ActiveProfiles("test")
@DisplayName("ProductSearchRepositoryImplElk Unit 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductSearchRepositoryImplElkTest {
    @Autowired
    private ProductSearchRepositoryImplElk productSearchRepository;
    private Long TEST_SALE_ID = 500L;
    private Long TEST_CATEGORY_ID = 500L;
    private int PRODUCT_COUNT = 5;
    private String TEST_EXPLANATION = "testExplanation";

    @BeforeEach
    public void generateTestProduct() {
//      TODO : elasticsearch 로 test code 수정중
//
//        //given
//        for (int i = 0; i < PRODUCT_COUNT; i++) {
//            Product product = Product.builder()
//                    .saleId(TEST_SALE_ID + i)
//                    .productName("testProductName" + i)
//                    .categoryId(TEST_CATEGORY_ID)
//                    .explanation(TEST_EXPLANATION)
//                    .productRegisterTime(LocalDateTime.now())
//                    .startPrice(1000)
//                    .startTime(LocalDateTime.now())
//                    .endTime(LocalDateTime.now())
//                    .highestPrice(1000000)
//                    .productStatus(ProductStatus.PRODUCT_REGISTRATION)
//                    .build();
//
//            productSearchRepository.save(product);
//        }
    }

    @Test
    @DisplayName("다양한 상품 검색 테스트")
    void searchProducts() {
        Pageable pageable = PageRequest.of(1, 10);
        //productName search when
        Page<DocumentProduct> searchResult = productSearchRepository.searchProducts("1", null, null,
                null, pageable, ProductSortOrder.BIDDER_COUNT_DESC);
        List<DocumentProduct> searchResultList = searchResult.getContent();

        //productName search then
        assertNotNull(searchResultList);
        assertEquals(1, searchResultList.size());
        for (int i = 0; i < searchResultList.size(); i++) {
            assertTrue(searchResultList.get(i).getProductName().contains("1"));
        }

        //saleId search when
        searchResult = productSearchRepository.searchProducts(null, TEST_SALE_ID, null,
                null, pageable, ProductSortOrder.BID_PRICE_ASC);
        searchResultList = searchResult.getContent();

        //saleId search then
        assertNotNull(searchResultList);
        assertEquals(1, searchResultList.size());
        for (int i = 0; i < searchResultList.size(); i++) {
            assertEquals(TEST_SALE_ID, searchResultList.get(i).getSaleId());
        }

        //categoryId search when
        searchResult = productSearchRepository.searchProducts(null, null, TEST_CATEGORY_ID,
                null, pageable, ProductSortOrder.BID_PRICE_ASC);
        searchResultList = searchResult.getContent();

        //categoryId search then
        assertNotNull(searchResultList);
        assertEquals(PRODUCT_COUNT, searchResultList.size());
        for (int i = 0; i < searchResultList.size(); i++) {
            assertEquals(TEST_CATEGORY_ID, searchResultList.get(i).getCategoryId());
        }

        //explanation search when
        searchResult = productSearchRepository.searchProducts(null, null, null,
                TEST_EXPLANATION, pageable, ProductSortOrder.BID_PRICE_ASC);
        searchResultList = searchResult.getContent();

        //explanation search then
        assertNotNull(searchResultList);
        assertEquals(PRODUCT_COUNT, searchResultList.size());
        for (int i = 0; i < searchResultList.size(); i++) {
            assertEquals(TEST_EXPLANATION, searchResultList.get(i).getExplanation());
        }

    }

}