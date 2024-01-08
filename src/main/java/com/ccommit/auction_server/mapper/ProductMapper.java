package com.ccommit.auction_server.mapper;

import com.ccommit.auction_server.dto.ProductDTO;
import com.ccommit.auction_server.model.ELK.DocumentProduct;
import com.ccommit.auction_server.model.Product;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ModelMapper modelMapper;

    public Product convertToEntity(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        return product;
    }

    public ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        return productDTO;
    }

    public ProductDTO convertToSearchDTO(DocumentProduct documentProduct) {
        ProductDTO searchProductDTO = modelMapper.map(documentProduct, ProductDTO.class);
        return searchProductDTO;
    }

}
