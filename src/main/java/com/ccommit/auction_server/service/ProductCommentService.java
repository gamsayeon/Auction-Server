package com.ccommit.auction_server.service;

import com.ccommit.auction_server.dto.ProductCommentDTO;

public interface ProductCommentService {

    ProductCommentDTO registerProductComment(Long userId, Long productId, ProductCommentDTO productCommentDTO);
}
