package com.ccommit.auction_server.controller;

import com.ccommit.auction_server.dto.CategoryDTO;
import com.ccommit.auction_server.service.serviceImpl.CategoryServiceImpl;
import com.ccommit.auction_server.aop.LoginCheck;
import com.ccommit.auction_server.dto.CategoryUpdateDTO;
import com.ccommit.auction_server.model.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/category")
@RestController
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Category API", description = "Category 관련 API")
public class CategoryController {

    private final Logger logger = LogManager.getLogger(CategoryController.class);

    private final CategoryServiceImpl categoryService;

    @PostMapping
    @LoginCheck(types = LoginCheck.LoginType.ADMIN)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "COMMON_NOT_MATCHING_MAPPER : 매핑 실패<br>" +
                    "CATEGORY_ADD_FAILED : 카테고리 등록 오류<br>" +
                    "CATEGORY_DUPLICATE_NAME : 카테고리 중복 오류", content = @Content),
            @ApiResponse(responseCode = "200", description = "카테고리 등록 성공", content = @Content(schema = @Schema(implementation = CategoryDTO.class)))
    })
    @Operation(summary = "Category 등록",
            description = "관리자로 인해 카테고리를 등록합니다. 하단의 CategoryDTO 참고",
            method = "POST",
            tags = "Category API",
            operationId = "Register Category")
    public ResponseEntity<CommonResponse<CategoryDTO>> registerCategory(@Parameter(hidden = true) Long loginId, @RequestBody @Valid CategoryDTO categoryDTO,
                                                                        HttpServletRequest request) {
        logger.debug("category을 등록합니다.");
        CommonResponse<CategoryDTO> response = new CommonResponse<>("SUCCESS", "Category를 등록했습니다.",
                request.getRequestURI(), categoryService.registerCategory(categoryDTO));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{categoryId}")
    @LoginCheck(types = LoginCheck.LoginType.ADMIN)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "CATEGORY_NOT_MATCH_ID : 해당하는 카테고리 없음<br>" +
                    "CATEGORY_UPDATE_FAILED : 카테고리 수정 실패<br>" +
                    "CATEGORY_DUPLICATE_NAME : 카테고리 중복 오류", content = @Content),
            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공", content = @Content(schema = @Schema(implementation = CategoryDTO.class)))
    })
    @Operation(summary = "Category 수정",
            description = "관리자로 인해 카테고리를 수정합니다.",
            method = "PATCH",
            tags = "Category API",
            operationId = "Update Category")
    @Parameter(name = "categoryId", description = "수정할 카테고리 식별자", example = "1")
    public ResponseEntity<CommonResponse<CategoryDTO>> updateCategory(@Parameter(hidden = true) Long loginId,
                                                                      @PathVariable("categoryId") Long categoryId,
                                                                      @RequestBody @Valid CategoryUpdateDTO categoryDTO,
                                                                      HttpServletRequest request) {
        logger.debug("category을 수정합니다.");
        CommonResponse<CategoryDTO> response = new CommonResponse<>("SUCCESS", "Category를 수정했습니다.",
                request.getRequestURI(), categoryService.updateCategory(categoryDTO, categoryId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "CATEGORY_NOT_MATCH_ID : 해당하는 카테고리 없음<br>" +
                    "CATEGORY_DUPLICATE_NAME : 카테고리 중복 오류", content = @Content),
            @ApiResponse(responseCode = "200", description = "카테고리 조회 성공", content = @Content(schema = @Schema(implementation = CategoryDTO.class)))
    })
    @Operation(summary = "Category 조회",
            description = "카테고리를 조회합니다.",
            method = "GET",
            tags = "Category API",
            operationId = "Select Category")
    @Parameter(name = "categoryId", description = "조회할 카테고리 식별자", example = "1")
    public ResponseEntity<CommonResponse<CategoryDTO>> selectCategory(@PathVariable("categoryId") Long categoryId,
                                                                      HttpServletRequest request) {
        logger.debug("category를 조회합니다.");
        CommonResponse<CategoryDTO> response = new CommonResponse<>("SUCCESS", "Category를 조회했습니다.",
                request.getRequestURI(), categoryService.selectCategory(categoryId));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    @LoginCheck(types = LoginCheck.LoginType.ADMIN)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "CATEGORY_NOT_MATCH_ID : 해당하는 카테고리 없음<br>" +
                    "CATEGORY_DELETE_FAILED : 카테고리 삭제 실패<br>" +
                    "CATEGORY_DUPLICATE_NAME : 카테고리 중복 오류", content = @Content),
            @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공", content = @Content(schema = @Schema(implementation = CategoryDTO.class)))
    })
    @Operation(summary = "Category 삭제",
            description = "관리자로 인해 카테고리를 삭제합니다.",
            method = "DELETE",
            tags = "Category API",
            operationId = "Delete Category")
    @Parameter(name = "categoryId", description = "삭제할 카테고리 식별자", example = "1")
    public ResponseEntity<CommonResponse<String>> deleteCategory(@Parameter(hidden = true) Long loginId,
                                                                 @PathVariable("categoryId") Long categoryId,
                                                                 HttpServletRequest request) {
        logger.debug("category을 삭제합니다.");
        String categoryName = categoryService.deleteCategory(categoryId);
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "Category를 삭제 했습니다.",
                request.getRequestURI(), categoryName + "은 정상적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}
