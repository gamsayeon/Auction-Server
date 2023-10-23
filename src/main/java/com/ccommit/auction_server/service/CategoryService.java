package com.ccommit.auction_server.service;

import com.ccommit.auction_server.dto.CategoryDTO;
import com.ccommit.auction_server.dto.CategoryUpdateDTO;

public interface CategoryService {
    CategoryDTO registerCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(CategoryUpdateDTO categoryDTO, Long categoryId);

    CategoryDTO selectCategory(Long categoryId);

    String deleteCategory(Long categoryId);
}
