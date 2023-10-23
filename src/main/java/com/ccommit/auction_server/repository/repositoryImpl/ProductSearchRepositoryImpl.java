package com.ccommit.auction_server.repository.repositoryImpl;

import com.ccommit.auction_server.repository.ProductSearchRepository;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.model.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductSearchRepositoryImpl implements ProductSearchRepository {
    private EntityManager entityManager;

    public ProductSearchRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Product> searchProducts(String productName, Long saleId, Long categoryId, String explanation, int page, int pageSize) {
        JPAQuery<Product> query = new JPAQuery<>(entityManager);

        QProduct product = QProduct.product;

        BooleanExpression whereClause = this.searchWhereClause(productName, saleId, categoryId, explanation);

        return query.select(product).from(product).where(whereClause).limit(pageSize).offset((page - 1) * pageSize).fetch();
    }

    public BooleanExpression searchWhereClause(String productName, Long saleId, Long categoryId, String explanation) {
        QProduct product = QProduct.product;
        BooleanExpression whereClause = null;

        if (productName != null) {
            if (whereClause == null) {
                whereClause = product.productName.contains(productName);
            } else {
                whereClause = whereClause.and(product.productName.contains(productName));
            }
        }

        if (saleId != null) {
            if (whereClause == null) {
                whereClause = product.saleId.eq(saleId);
            } else {
                whereClause = whereClause.and(product.saleId.eq(saleId));
            }
        }

        if (categoryId != null) {
            if (whereClause == null) {
                whereClause = product.categoryId.eq(categoryId);
            } else {
                whereClause = whereClause.and(product.categoryId.eq(categoryId));
            }
        }

        if (explanation != null) {
            if (whereClause == null) {
                whereClause = product.explanation.contains(explanation);
            } else {
                whereClause = whereClause.and(product.explanation.contains(explanation));
            }
        }
        return whereClause;
    }

}
