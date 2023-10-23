package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.dto.CategoryDTO;
import com.ccommit.auction_server.mapper.CategoryMapper;
import com.ccommit.auction_server.model.Category;
import com.ccommit.auction_server.repository.CategoryRepository;
import com.ccommit.auction_server.dto.CategoryUpdateDTO;
import com.ccommit.auction_server.exception.AddFailedException;
import com.ccommit.auction_server.exception.DeleteFailedException;
import com.ccommit.auction_server.exception.NotMatchingException;
import com.ccommit.auction_server.exception.UpdateFailedException;
import com.ccommit.auction_server.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final int DELETE_SUCCESS = 1;
    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);

    @Override
    public CategoryDTO registerCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.convertToEntity(categoryDTO);

        Category resultCategory = categoryRepository.save(category);
        if (resultCategory != null) {
            CategoryDTO resultCategoryDTO = categoryMapper.convertToDTO(resultCategory);
            if (resultCategoryDTO != null) {
                logger.info(resultCategoryDTO.getCategoryName() + "을 카테고리 등록에 성공했습니다.");
                return resultCategoryDTO;
            } else {
                logger.warn("매핑에 실패했습니다.");
                throw new NotMatchingException("COMMON_NOT_MATCHING_MAPPER", categoryDTO);
            }
        } else {
            logger.warn("category 등록 오류. 재시도 해주세요.");
            throw new AddFailedException("CATEGORY_ADD_FAILED", categoryDTO);
        }
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(CategoryUpdateDTO categoryDTO, Long categoryId) {
        Category category = categoryMapper.convertToEntity(categoryDTO);
        Optional<Category> optionalCategory = categoryRepository.findByCategoryId(categoryId);
        if (optionalCategory.isEmpty()) {
            logger.warn("해당하는 카테고리는 찾지 못했습니다.");
            throw new NotMatchingException("CATEGORY_NOT_MATCH_ID", categoryId);
        } else {
            category.setCategoryId(optionalCategory.get().getCategoryId());
            Category resultCategory = categoryRepository.save(category);
            if (resultCategory == null) {
                logger.warn("카테고리를 수정하지 못했습니다.");
                throw new UpdateFailedException("CATEGORY_UPDATE_FAILED", categoryDTO);
            } else {
                CategoryDTO resultCategoryDTO = categoryMapper.convertToDTO(resultCategory);
                logger.info(resultCategoryDTO.getCategoryName() + "을 카테고리 수정에 성공했습니다.");
                return resultCategoryDTO;
            }
        }
    }

    @Override
    public CategoryDTO selectCategory(Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findByCategoryId(categoryId);
        if (optionalCategory.isEmpty()) {
            logger.warn("해당하는 카테고리는 찾지 못했습니다.");
            throw new NotMatchingException("CATEGORY_NOT_MATCH_ID", categoryId);
        } else {
            CategoryDTO resultCategoryDTO = categoryMapper.convertToDTO(optionalCategory.get());
            logger.info("카테고리 조회에 성공했습니다.");
            return resultCategoryDTO;
        }
    }

    @Override
    @Transactional
    public String deleteCategory(Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findByCategoryId(categoryId);
        if (optionalCategory.get() != null) {
            if (categoryRepository.deleteByCategoryId(categoryId) == DELETE_SUCCESS) {
                String categoryName = optionalCategory.get().getCategoryName();
                logger.info(categoryName + "을 카테고리 삭제에 성공했습니다.");
                return categoryName;
            } else {
                logger.warn("category 삭제 실패 오류. 재시도 해주세요.");
                throw new DeleteFailedException("CATEGORY_DELETE_FAILED", categoryId);
            }
        } else {
            logger.warn("해당하는 카테고리는 찾지 못했습니다.");
            throw new NotMatchingException("CATEGORY_NOT_MATCH_ID", categoryId);
        }
    }

}