package com.ccommit.auction_server.mapper;

import com.ccommit.auction_server.dto.ProductDTO;
import com.ccommit.auction_server.dto.ProductImageDTO;
import com.ccommit.auction_server.model.ProductImage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductImageMapper {
    private final ModelMapper modelMapper;

    public List<ProductImage> convertToEntity(ProductDTO productDTO, Long productId) {
        List<ProductImage> productImages = new ArrayList<>();
        for (ProductImageDTO productImageDTO : productDTO.getImageDTOS()) {
            ProductImage productImage = modelMapper.map(productImageDTO, ProductImage.class);
            productImage.setProductId(productId);
            productImages.add(productImage);
        }
        return productImages;
    }

    public List<ProductImageDTO> convertToDTO(List<ProductImage> productImages) {
        List<ProductImageDTO> productImageDTOs = new ArrayList<>();
        for (ProductImage productImage : productImages) {
            ProductImageDTO productImageDTO = modelMapper.map(productImage, ProductImageDTO.class);
            productImageDTOs.add(productImageDTO);
        }
        return productImageDTOs;
    }
}
