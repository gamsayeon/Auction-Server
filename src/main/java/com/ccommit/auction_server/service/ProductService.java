package com.ccommit.auction_server.service;

import com.ccommit.auction_server.dto.ProductDTO;
import com.ccommit.auction_server.dto.SearchProductDTO;
import com.ccommit.auction_server.enums.ProductSortOrder;

import java.util.List;

public interface ProductService {
    ProductDTO registerProduct(Long id, ProductDTO productDTO);

    ProductDTO selectProduct(Long productId);

    List<ProductDTO> selectProductForUser(Long saleId);

    ProductDTO updateProduct(Long saleId, Long productId, ProductDTO productDTO);

    void updateProductStatus();

    void deleteProduct(Long saleId, Long productId);

    SearchProductDTO findByKeywordELK(String productName, Long saleId, Long categoryId,
                                      String explanation, int page, int pageSize, ProductSortOrder sortOrder);
}
