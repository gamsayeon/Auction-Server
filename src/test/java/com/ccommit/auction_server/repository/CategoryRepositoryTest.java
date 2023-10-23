package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CategoryRepository Unit 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private String TEST_CATEGORY_NAME = "testCategoryName";
    private int DELETE_SUCCESS = 1;
    private int TEST_BID_MIN_PRICE = 1000;
    private Long savedCategoryId;

    @BeforeEach
    public void generateTestCategory() {
        //given
        Category category = Category.builder()
                .categoryName(TEST_CATEGORY_NAME)
                .bidMinPrice(TEST_BID_MIN_PRICE)
                .build();

        savedCategoryId = categoryRepository.save(category).getCategoryId();
    }

    @Test
    @DisplayName("카테고리 식별자로 카테고리 조회")
    void findByCategoryId() {
        //when
        Optional<Category> findCategory = categoryRepository.findByCategoryId(savedCategoryId);

        //then
        assertNotNull(findCategory);
        assertEquals(TEST_CATEGORY_NAME, findCategory.get().getCategoryName());
    }

    @Test
    @DisplayName("카테고리 식별자로 카테고리 삭제")
    void deleteByCategoryId() {
        //when
        int deleteCategory = categoryRepository.deleteByCategoryId(savedCategoryId);

        //then
        assertTrue(categoryRepository.findByCategoryId(savedCategoryId).isEmpty());
        assertEquals(DELETE_SUCCESS, deleteCategory);
    }

    @Test
    @DisplayName("카테고리명 중복 검사")
    void existsByCategoryName() {
        //when
        boolean existsCategoryName = categoryRepository.existsByCategoryName(TEST_CATEGORY_NAME);

        //then
        assertTrue(existsCategoryName);
    }

    @Test
    @DisplayName("유효한 카테고리 식별자 확인")
    void existsByCategoryId() {
        //when
        boolean existsCategoryId = categoryRepository.existsByCategoryId(savedCategoryId);

        //then
        assertTrue(existsCategoryId);
    }
}