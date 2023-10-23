package com.ccommit.auction_server.enums;

public enum ProductSortOrder {
    BIDDER_COUNT_DESC,          // 입찰자가 많은 순
    HIGHEST_PRICE_DESC,         // 즉시 가격 높은 순
    HIGHEST_PRICE_ASC,          // 즉시 가격 낮은 순
    BID_PRICE_DESC,                 // 가격 높은 순
    BID_PRICE_ASC,                  // 가격 낮은 순
    NEWEST_FIRST,               // 등록일 최신 순
    OLDEST_FIRST                // 등록일 과거 순
}
