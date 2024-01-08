package com.ccommit.auction_server.controller;

import com.ccommit.auction_server.aop.LoginCheck;
import com.ccommit.auction_server.dto.BidDTO;
import com.ccommit.auction_server.model.CommonResponse;
import com.ccommit.auction_server.service.serviceImpl.BidServiceImpl;
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

import java.util.List;

@RequestMapping("/bids")
@RestController
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Bid API", description = "Bid 관련 API")
public class BidController {
    private final BidServiceImpl bidService;

    private final Logger logger = LogManager.getLogger(BidController.class);

    @PostMapping("/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "BID_FAILED_NOT_START : 경매가 시작하지 않음<br>" +
                    "BID_INPUT_MISMATCH : 입찰 값을 잘못입력시", content = @Content),
            @ApiResponse(responseCode = "200", description = "입찰 등록 성공", content = @Content(schema = @Schema(implementation = BidDTO.class)))
    })
    @Operation(summary = "Bid 등록",
            description = "구매자가 입찰을 합니다. 하단의 BidDTO 참고",
            method = "POST",
            tags = "Bid API",
            operationId = "Register Bid")
    @Parameter(name = "productId", description = "입찰할 상품 식별자", example = "1")
    public ResponseEntity<CommonResponse<BidDTO>> registerBid(@Parameter(hidden = true) Long loginId, @PathVariable("productId") Long productId,
                                                              @RequestBody @Valid BidDTO bidDTO, HttpServletRequest request) {
        logger.info("경매에 입찰합니다.");
        CommonResponse<BidDTO> response = new CommonResponse<>("SUCCESS", "경매에 입찰했습니다.",
                request.getRequestURI(), bidService.registerBid(loginId, productId, bidDTO));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/histories")
    @LoginCheck(types = LoginCheck.LoginType.USER)
    public ResponseEntity<CommonResponse<List<BidDTO>>> selectBidByUser(@Parameter(hidden = true) Long loginId,
                                                                        @RequestParam(value = "productId", required = false) Long productId,
                                                                        HttpServletRequest request) {
        logger.info("경매 이력을 조회합니다.");
        CommonResponse<List<BidDTO>> response = new CommonResponse<>("SUCCESS", "경매를 조회했습니다.",
                request.getRequestURI(), bidService.selectBidByUserId(loginId, productId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/histories/{productId}")
    public ResponseEntity<CommonResponse<List<BidDTO>>> selectBidByProduct(@PathVariable("productId") Long productId,
                                                                           HttpServletRequest request) {
        logger.info("판매자의 경매 이력을 조회합니다.");
        CommonResponse<List<BidDTO>> response = new CommonResponse<>("SUCCESS", "경매를 조회했습니다.",
                request.getRequestURI(), bidService.selectBidByProduct(productId));
        return ResponseEntity.ok(response);
    }
}
