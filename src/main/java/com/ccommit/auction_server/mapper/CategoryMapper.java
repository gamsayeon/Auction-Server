package com.ccommit.auction_server.mapper;

import com.ccommit.auction_server.model.Category;
import com.ccommit.auction_server.dto.CategoryDTO;
import com.ccommit.auction_server.dto.CategoryUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {
    private final ModelMapper modelMapper;

    public Category convertToEntity(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        return category;
    }

    public Category convertToEntity(CategoryUpdateDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        return category;
    }

    public CategoryDTO convertToDTO(Category category) {
        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
        return categoryDTO;
    }
}
