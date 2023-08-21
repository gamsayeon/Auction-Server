package com.example.auction_server.mapper;

import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.dto.ProductImageDTO;
import com.example.auction_server.model.ProductImage;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductImageMapper {
    private final ModelMapper modelMapper;

    public ProductImageMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    public List<ProductImage> convertToEntity(ProductDTO productDTO) {
        List<ProductImage> productImages = new ArrayList<>();
        for (ProductImageDTO productImageDTO : productDTO.getImageDTOS()) {
            ProductImage productImage = modelMapper.map(productImageDTO, ProductImage.class);
            productImage.setProductId(productDTO.getProductId());
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
