package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.mapper.ProductCommentMapper;
import com.ccommit.auction_server.dto.ProductCommentDTO;
import com.ccommit.auction_server.exception.AddFailedException;
import com.ccommit.auction_server.model.ProductComment;
import com.ccommit.auction_server.repository.ProductCommentRepository;
import com.ccommit.auction_server.service.ProductCommentService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductCommentServiceImpl implements ProductCommentService {

    private final Logger logger = LogManager.getLogger(ProductCommentServiceImpl.class);

    private final ProductCommentRepository productCommentRepository;
    private final ProductCommentMapper productCommentMapper;

    @Override
    public ProductCommentDTO registerProductComment(Long userId, Long productId, ProductCommentDTO productCommentDTO) {
        ProductComment productComment = productCommentMapper.convertToEntity(productCommentDTO);
        productComment.setProductId(productId);
        productComment.setUserId(userId);
        productComment.setCreateTime(LocalDateTime.now());

        ProductComment resultProductComment = productCommentRepository.save(productComment);
        if (resultProductComment != null) {
            ProductCommentDTO resultProductCommentDTO = productCommentMapper.convertToDTO(resultProductComment);
            logger.info("댓글을 등록했습니다.");
            return resultProductCommentDTO;
        } else {
            logger.warn("댓글을 등록하지 못했습니다.");
            throw new AddFailedException("PRODUCT_COMMENT_ADD_FAILED", productCommentDTO);
        }
    }
}