package com.ccommit.auction_server.service;

import com.ccommit.auction_server.dto.BidDTO;

import java.util.List;

public interface BidService {
    BidDTO registerBid(Long buyerId, Long productId, BidDTO bidDTO);

    List<BidDTO> selectBidByUserId(Long buyerId, Long productId);

    List<BidDTO> selectBidByProduct(Long productId);
}
