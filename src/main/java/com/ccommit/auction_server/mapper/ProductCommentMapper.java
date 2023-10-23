package com.ccommit.auction_server.mapper;

import com.ccommit.auction_server.dto.ProductCommentDTO;
import com.ccommit.auction_server.model.ProductComment;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCommentMapper {
    private final ModelMapper modelMapper;

    public ProductComment convertToEntity(ProductCommentDTO productCommentDTO) {
        ProductComment productComment = modelMapper.map(productCommentDTO, ProductComment.class);
        return productComment;
    }

    public ProductCommentDTO convertToDTO(ProductComment productComment) {
        ProductCommentDTO productCommentDTO = modelMapper.map(productComment, ProductCommentDTO.class);
        return productCommentDTO;
    }
}
