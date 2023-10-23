package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductSearchRepository {
    Product findByProductId(Long productId);

    List<Product> findBySaleId(Long saleId);

    int deleteByProductId(Long productId);

    @Query("SELECT p FROM product p WHERE p.productStatus = :productStatusValue1 OR p.productStatus = :productStatusValue2")
    List<Product> findByProductStatus(@Param("productStatusValue1") ProductStatus productStatusValue1,
                                      @Param("productStatusValue2") ProductStatus productStatusValue2);

    boolean existsByProductId(Long productId);
}