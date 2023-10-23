package com.ccommit.auction_server.controller;

import com.ccommit.auction_server.aop.LoginCheck;
import com.ccommit.auction_server.dto.ProductCommentDTO;
import com.ccommit.auction_server.dto.ProductDTO;
import com.ccommit.auction_server.dto.SearchProductDTO;
import com.ccommit.auction_server.enums.ProductSortOrder;
import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.model.CommonResponse;
import com.ccommit.auction_server.service.serviceImpl.ProductCommentServiceImpl;
import com.ccommit.auction_server.service.serviceImpl.ProductServiceImpl;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Product 관련 API")
public class ProductController {

    private final Logger logger = LogManager.getLogger(ProductController.class);
    private final ProductServiceImpl productService;
    private final ProductCommentServiceImpl productCommentService;

    @PostMapping
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "PRODUCT_ADD_FAILED : 상품 등록 실패<br>" +
                    "PRODUCT_IMAGE_ADD_FAILED : 상품 이미지 등록 실패<br>" +
                    "CATEGORY_NOT_MATCH_ID : 해당하는 카테고리 없음<br>" +
                    "PRODUCT_INPUT_MISMATCH_TIME : 상품 경매 시간값 잘못 입력", content = @Content),
            @ApiResponse(responseCode = "200", description = "상품 등록 성공", content = @Content(schema = @Schema(implementation = ProductDTO.class)))
    })
    @Operation(summary = "Product 등록",
            description = "판매자가 상품을 등록합니다. 하단의 ProductDTO, ProductImageDTO 참고",
            method = "POST",
            tags = "Product API",
            operationId = "Register Product")
    public ResponseEntity<CommonResponse<ProductDTO>> registerProduct(@Parameter(hidden = true) Long loginId, @RequestBody @Valid ProductDTO productDTO,
                                                                      HttpServletRequest request) {
        logger.debug("상품을 등록합니다.");
        CommonResponse<ProductDTO> response = new CommonResponse<>("SUCCESS", "상품을 등록했습니다.",
                request.getRequestURI(), productService.registerProduct(loginId, productDTO));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "PRODUCT_ACCESS_DENIED_SELECT : 상품 조회 권한 없음<br>" +
                    "PRODUCT_NOT_MATCH_ID : 상품 조회 실패", content = @Content),
            @ApiResponse(responseCode = "200", description = "상품 조회 성공", content = @Content(schema = @Schema(implementation = ProductDTO.class)))
    })
    @Operation(summary = "Product 조회",
            description = "상품을 조회합니다.",
            method = "GET",
            tags = "Product API",
            operationId = "Select Product")
    @Parameter(name = "productId", description = "조회할 상품 식별자", example = "1")
    public ResponseEntity<CommonResponse<ProductDTO>> selectProduct(@PathVariable("productId") Long productId,
                                                                    HttpServletRequest request) {
        logger.debug("상품을 조회합니다.");
        CommonResponse<ProductDTO> response = new CommonResponse<>("SUCCESS", "상품을 조회했습니다.",
                request.getRequestURI(), productService.selectProduct(productId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "PRODUCT_NOT_MATCH_ID : 자신의 상품 조회 실패", content = @Content),
            @ApiResponse(responseCode = "200", description = "자신의 상품 조회 성공", content = @Content(schema = @Schema(implementation = ProductDTO.class)))
    })
    @Operation(summary = "판매자의 Product 조회",
            description = "판매자가 등록한 상품을 조회합니다.",
            method = "GET",
            tags = "Product API",
            operationId = "Select Product For User")
    public ResponseEntity<CommonResponse<List<ProductDTO>>> selectProductForUser(@Parameter(hidden = true) Long loginId, HttpServletRequest request) {
        logger.debug("상품을 조회합니다.");
        CommonResponse<List<ProductDTO>> response = new CommonResponse<>("SUCCESS", "상품을 조회했습니다.",
                request.getRequestURI(), productService.selectProductForUser(loginId));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "PRODUCT_UPDATE_FAILED_BY_STATUS : 상품이 시작되어 수정 실패<br>" +
                    "PRODUCT_UPDATE_FAILED : 상품 수정 실패<br>" +
                    "PRODUCT_ACCESS_DENIED : 상품 수정의 권한이 없음", content = @Content),
            @ApiResponse(responseCode = "200", description = "상품 수정 성공", content = @Content(schema = @Schema(implementation = ProductDTO.class)))
    })
    @Operation(summary = "Product 수정",
            description = "판매자가 등록한 상품을 수정합니다.",
            method = "PATCH",
            tags = "Product API",
            operationId = "Update Product")
    @Parameter(name = "productId", description = "수정할 상품 식별자", example = "1")
    public ResponseEntity<CommonResponse<ProductDTO>> updateProduct(@Parameter(hidden = true) Long loginId, @PathVariable("productId") Long productId,
                                                                    @RequestBody @Valid ProductDTO productDTO,
                                                                    HttpServletRequest request) {
        logger.debug("상품을 수정합니다.");
        CommonResponse<ProductDTO> response = new CommonResponse<>("SUCCESS", "상품을 수정했습니다.",
                request.getRequestURI(), productService.updateProduct(loginId, productId, productDTO));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/withdraw/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "PRODUCT_UPDATE_FAILED_BY_STATUS : 상품의 상태로 인한 삭제 실패<br>" +
                    "PRODUCT_DELETE_FAILED : 상품의 삭제 실패<br>" +
                    "COMMON_ACCESS_DENIED : 상품의 권한 없음<br>" +
                    "PRODUCT_IMAGE_DELETE_FAILED : 상품의 이미지 삭제 실패", content = @Content),
            @ApiResponse(responseCode = "200", description = "자신의 상품 삭제 성공", content = @Content(schema = @Schema(implementation = ProductDTO.class)))
    })
    @Operation(summary = "판매자의 Product 삭제",
            description = "판매자가 등록한 상품을 삭제합니다.",
            method = "DELETE",
            tags = "Product API",
            operationId = "Delete Product")
    @Parameter(name = "productId", description = "삭제할 상품 식별자", example = "1")
    public ResponseEntity<CommonResponse<String>> deleteProduct(@Parameter(hidden = true) Long loginId, @PathVariable("productId") Long productId,
                                                                HttpServletRequest request) {
        logger.debug("상품을 삭제합니다.");
        productService.deleteProduct(loginId, productId);
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "상품을 삭제했습니다.",
                request.getRequestURI(), productId + " 정상적으로 상품이 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

    @Scheduled(cron = "${auction.time.interval}")
    public void updateProductAuctionStatus() {
        logger.debug("경매상태값을 수정합니다.");
        productService.updateProductStatus(ProductStatus.PRODUCT_REGISTRATION);
        productService.updateProductStatus(ProductStatus.AUCTION_PROCEEDING);
    }

    @PostMapping("/comments/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "PRODUCT_COMMENT_ADD_FAILED : 상품 댓글 등록 실패", content = @Content),
            @ApiResponse(responseCode = "200", description = "상품 댓글 등록 성공", content = @Content(schema = @Schema(implementation = ProductCommentDTO.class)))
    })
    @Operation(summary = "Product 댓글 등록",
            description = "등록한 상품에 댓글을 등록합니다.",
            method = "POST",
            tags = "Product API",
            operationId = "Register Product Comment")
    @Parameter(name = "productId", description = "댓글을 등록할 상품 식별자", example = "1")
    public ResponseEntity<CommonResponse<ProductCommentDTO>> registerProductComment(@Parameter(hidden = true) Long loginId, @PathVariable("productId") Long productId,
                                                                                    @RequestBody @Valid ProductCommentDTO productCommentDTO,
                                                                                    HttpServletRequest request) {
        logger.info("상품 댓글을 등록합니다.");
        CommonResponse<ProductCommentDTO> response = new CommonResponse<>("SUCCESS", "상품에 댓글을 추가했습니다.",
                request.getRequestURI(), productCommentService.registerProductComment(loginId, productId, productCommentDTO));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 검색 성공", content = @Content(schema = @Schema(implementation = ProductDTO.class)))
    })
    @Operation(summary = "Product 검색",
            description = "해당하는 키워드로 상품 검색(상품명, 판매자, 카테고리, 상품 설명) 및 원하는 순서로 반환",
            method = "GET",
            tags = "Product API",
            operationId = "Search Product By Sort")
    public ResponseEntity<CommonResponse<SearchProductDTO>> searchProduct(@RequestParam(value = "productName", required = false) String productName,
                                                                          @RequestParam(value = "saleId", required = false) Long saleId,
                                                                          @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                                          @RequestParam(value = "explanation", required = false) String explanation,
                                                                          @RequestParam(name = "page", defaultValue = "1") int page,
                                                                          @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                          @RequestParam(value = "sortOrder", defaultValue = "BIDDER_COUNT_DESC", required = false) ProductSortOrder sortOrder,
                                                                          HttpServletRequest request) {
        logger.debug("상품을 검색합니다.");
        CommonResponse<SearchProductDTO> response = new CommonResponse<>("SUCCESS", "상품검색에 성공했습니다.",
                request.getRequestURI(), productService.findByKeyword(productName, saleId, categoryId, explanation, page, pageSize, sortOrder));
        return ResponseEntity.ok(response);
    }
}
