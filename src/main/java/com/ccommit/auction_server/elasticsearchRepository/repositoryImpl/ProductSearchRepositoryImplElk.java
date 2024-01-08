package com.ccommit.auction_server.elasticsearchRepository.repositoryImpl;

import com.ccommit.auction_server.elasticsearchRepository.ProductSearchRepository;
import com.ccommit.auction_server.enums.ProductSortOrder;
import com.ccommit.auction_server.model.ELK.DocumentProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


@Repository
@RequiredArgsConstructor
public class ProductSearchRepositoryImplElk implements ProductSearchRepository {
    private final ElasticsearchOperations operations;

    @Override
    public Page<DocumentProduct> searchProducts(String productName, Long saleId, Long categoryId, String explanation, Pageable pageable, ProductSortOrder sortOrder) {
        Sort sort = this.sortOrderToSort(sortOrder);
        CriteriaQuery query = createConditionCriteriaQuery(productName, saleId, categoryId, explanation, sort);

        SearchHits<DocumentProduct> searchHits = operations.search(query, DocumentProduct.class);

        return SearchHitSupport
                .searchPageFor(searchHits, pageable)
                .map(SearchHit::getContent);
    }

    public Sort sortOrderToSort(ProductSortOrder sortOrder) {
        Sort sort = null;
        switch (sortOrder) {
            case BIDDER_COUNT_DESC:             //입찰자가 많은 순
                sort = Sort.by(Sort.Order.asc("bid_count"));
                break;
            case HIGHEST_PRICE_DESC:     // 최고 즉시 구매가 순
                sort = Sort.by(Sort.Order.asc("highest_price"));
                break;
            case HIGHEST_PRICE_ASC:     // 최저 즉시 구매가 순
                sort = Sort.by(Sort.Order.desc("highest_price"));
                break;
            case BID_PRICE_DESC:             //최고 입찰가 순
                sort = Sort.by(Sort.Order.asc("max_bid_price"));
                break;
            case BID_PRICE_ASC:             //최저 입찰가 순
                sort = Sort.by(Sort.Order.desc("max_bid_price"));
                break;
            case NEWEST_FIRST:                  //등록일 최신 순
                sort = Sort.by(Sort.Order.asc("product_register_time"));
                break;
            case OLDEST_FIRST:                  //등록일 과거 순
                sort = Sort.by(Sort.Order.desc("product_register_time"));
                break;
        }
        return sort;
    }

    public CriteriaQuery createConditionCriteriaQuery(String productName, Long saleId, Long categoryId, String explanation, Sort sort) {
        Criteria baseCriteria = new Criteria();

        if (StringUtils.hasText(productName)) {
            baseCriteria = baseCriteria.and("product_name").is(productName);
        }

        if (saleId != null) {
            baseCriteria = baseCriteria.and("sale_id").is(saleId);
        }

        if (categoryId != null) {
            baseCriteria = baseCriteria.and("category_id").is(categoryId);
        }

        if (StringUtils.hasText(explanation)) {
            baseCriteria = baseCriteria.and("explanation").is(explanation);
        }
        CriteriaQuery query = new CriteriaQuery(baseCriteria);
        query.addSort(sort);

        return query;
    }
}
