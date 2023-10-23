package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryId(Long categoryId);

    int deleteByCategoryId(Long categoryId);

    boolean existsByCategoryName(String categoryName);

    boolean existsByCategoryId(Long categoryId);
}