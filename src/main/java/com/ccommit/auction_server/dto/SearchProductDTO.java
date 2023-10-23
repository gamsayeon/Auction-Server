package com.ccommit.auction_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "상품 검색 결과 DTO")
public class SearchProductDTO {
    @Schema(name = "page", description = "현재 검색 페이지", example = "1")
    private int page;

    @Schema(name = "pageSize", description = "한페이지 검색 결과 갯수", example = "10")
    private int pageSize;

    @Schema(name = "totalItems", description = "총 검색 결과")
    private int totalItems;

    @Schema(name = "totalPages", description = "총 페이지 결과")
    private int totalPages;

    @Schema(name = "productDTOs", description = "상품 검색 결과")
    private List<ProductDTO> productDTOs;
}