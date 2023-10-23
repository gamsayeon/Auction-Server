package com.ccommit.auction_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "상품 이미지 DTO")
public class ProductImageDTO {
    @Schema(name = "imageId", description = "상품 이미지 식별자(Auto Increment)", example = "1")
    private Long imageId;

    @Schema(name = "productId", description = "상품 식별자", example = "1")
    private Long productId;

    @Lob
    @Schema(name = "imagePath", description = "상품 이미지 경로", example = "C:\\")
    private String imagePath;
}