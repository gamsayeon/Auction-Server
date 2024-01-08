package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.dto.BidDTO;
import com.ccommit.auction_server.elasticsearchRepository.BidSelectRepository;
import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.exception.BidFailedNotStartException;
import com.ccommit.auction_server.exception.NotMatchingException;
import com.ccommit.auction_server.mapper.BidMapper;
import com.ccommit.auction_server.model.Bid;
import com.ccommit.auction_server.model.ELK.DocumentBid;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.repository.ProductRepository;
import com.ccommit.auction_server.service.BidPriceValidService;
import com.ccommit.auction_server.service.BidService;
import com.ccommit.auction_server.service.MQService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidMapper bidMapper;
    private final MQService rabbitMQService;
    private final ProductRepository productRepository;
    private final BidPriceValidService bidPriceValidService;
    private final BidSelectRepository bidSelectRepository;
    private static final Logger logger = LogManager.getLogger(BidServiceImpl.class);

    @Override
    public BidDTO registerBid(Long buyerId, Long productId, BidDTO bidDTO) {
        Product product = productRepository.findByProductId(productId);
        bidPriceValidService.validBidPrice(productId, bidDTO.getPrice());

        if (product.getProductStatus() != ProductStatus.AUCTION_PROCEEDING) {
            logger.warn("경매가 시작되지 않았습니다.");
            throw new BidFailedNotStartException("BID_FAILED_NOT_START");
        } else {
            Bid bid = bidMapper.convertToEntity(bidDTO, productId, buyerId);

            rabbitMQService.enqueueMassage(bid);

            return bidMapper.convertToDTO(bid);
        }
    }

    @Override
    public List<BidDTO> selectBidByUserId(Long buyerId, Long productId) {
        List<DocumentBid> bids;
        if (productId == null) {
            bids = bidSelectRepository.findByBuyerId(buyerId);
        } else if (!productRepository.existsByProductId(productId)) {
            logger.warn("해당하는 상품을 찾지 못했습니다.");
            throw new NotMatchingException("PRODUCT_NOT_MATCH_ID", productId);
        } else {
            bids = bidSelectRepository.findByBuyerIdAndProductId(buyerId, productId);
        }

        return bidMapper.convertToSelectBidDTOList(bids);
    }

    @Override
    public List<BidDTO> selectBidByProduct(Long productId) {
        if (!productRepository.existsByProductId(productId)) {
            logger.warn("해당하는 상품을 찾지 못했습니다.");
            throw new NotMatchingException("PRODUCT_NOT_MATCH_ID", productId);
        }
        List<DocumentBid> bids = bidSelectRepository.findByProductId(productId);

        return bidMapper.convertToSelectBidDTOList(bids);
    }
}