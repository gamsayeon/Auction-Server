package com.ccommit.auction_server.dto;

import com.ccommit.auction_server.validation.annotation.isExistCategoryValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "카테고리 업데이트 DTO")
public class CategoryUpdateDTO {
    @NotBlank
    @isExistCategoryValidation
    @Schema(name = "categoryName", description = "카테고리 명", example = "testUpdateCategoryName")
    private String categoryName;

    @Min(value = 1000)
    @Schema(name = "bidMinPrice", description = "카테고리 최소 입찰 금액 단위", example = "2000")
    private int bidMinPrice;

}