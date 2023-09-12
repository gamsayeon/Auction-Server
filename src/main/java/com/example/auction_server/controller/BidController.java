package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.BidDTO;
import com.example.auction_server.model.CommonResponse;
import com.example.auction_server.service.serviceImpl.BidServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/bid")
@RestController
@Log4j2
public class BidController {
    private final BidServiceImpl auctionService;

    private final Logger logger = LogManager.getLogger(BidController.class);

    public BidController(BidServiceImpl auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping("/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<CommonResponse<BidDTO>> registerBid(Long loginId, @PathVariable("productId") Long productId,
                                                              @RequestBody @Valid BidDTO bidDTO, HttpServletRequest request) {
        logger.info("경매에 입찰합니다.");
        CommonResponse<BidDTO> response = new CommonResponse<>("SUCCESS", "경매에 입찰했습니다.",
                request.getRequestURI(), auctionService.registerBid(loginId, productId, bidDTO));
        return ResponseEntity.ok(response);
    }
}
