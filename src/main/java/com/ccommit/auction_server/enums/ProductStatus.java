package com.ccommit.auction_server.enums;

public enum ProductStatus {
    PRODUCT_REGISTRATION,   //상품등록
    AUCTION_PROCEEDING, //경매 진행중
    AUCTION_END,    //경매 종료
    DELIVERING,     //배송중
    DELIVERY_COMPLETED, //배송완료
    AUCTION_PAUSE   //경매 일시 정지(관리자)
}
