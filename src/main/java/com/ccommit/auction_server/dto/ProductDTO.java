package com.ccommit.auction_server.dto;

import com.ccommit.auction_server.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "상품 DTO")
public class ProductDTO {
    @Schema(name = "saleId", description = "판매자 식별자", example = "1")
    private Long saleId;

    @NotBlank
    @Schema(name = "productName", description = "상품 명", example = "testProductName")
    private String productName;

    @NotNull
    @Schema(name = "categoryId", description = "카테고리 식별자", example = "1")
    private Long categoryId;

    @Lob
    @NotBlank
    @Schema(name = "explanation", description = "상품 설명", example = "testExplanation")
    private String explanation;

    @Schema(name = "productRegisterTime", description = "상품 등록 시간")
    private LocalDateTime productRegisterTime;

    @Min(value = 1000)
    @Schema(name = "startPrice", description = "상품 입찰 시작 가격", example = "1000")
    private int startPrice;

    @NotNull
    @Schema(name = "startTime", description = "입찰 시작 시간")
    private LocalDateTime startTime;

    @NotNull
    @Schema(name = "endTime", description = "입찰 종료 시간")
    private LocalDateTime endTime;

    @Min(value = 1000)
    @Schema(name = "highestPrice", description = "즉시구매가", example = "10000")
    private int highestPrice;

    @Schema(name = "productStatus", description = "상품 상태", example = "PRODUCT_REGISTRATION")
    private ProductStatus productStatus;

    @Schema(name = "imageDTOS", description = "상품 이미지")
    private List<ProductImageDTO> imageDTOS;

}