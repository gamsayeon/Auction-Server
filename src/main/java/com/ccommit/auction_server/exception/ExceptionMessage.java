package com.ccommit.auction_server.exception;

import java.util.HashMap;
import java.util.Map;

public class ExceptionMessage {

    private static final Map<String, String> exceptionMessages = new HashMap<>();

    static {
        //Common
        exceptionMessages.put("COMMON_NOT_MATCHING_MAPPER", "매핑에 실패했습니다. 조회해서 확인해주세요.");
        exceptionMessages.put("COMMON_LOGIN_REQUIRED", "User login required");
        exceptionMessages.put("COMMON_ACCESS_DENIED", "권한 부족");
        //User
        exceptionMessages.put("USER_ADD_FAILED", "회원가입에 성공하지 못했습니다. 다시 시도해주세요.");
        exceptionMessages.put("USER_DUPLICATE_ID", "중복된 ID 입니다. 다른 ID을 입력해주세요.");
        exceptionMessages.put("USER_DUPLICATE_EMAIL", "중복된 Email 입니다. 다른 Email을 입력해주세요.");
        exceptionMessages.put("USER_USER_ACCESS_DENIED", "ADMIN 으로 회원가입할 수 없습니다.");
        exceptionMessages.put("USER_NOT_MATCH_LOGIN", "로그인에 실패 했습니다. 아이디 및 비밀번호 확인 후 재시도 해주세요.");
        exceptionMessages.put("USER_NOT_MATCH", "해당 유저를 찾지 못했습니다. 재시도 해주세요.");
        exceptionMessages.put("USER_NOT_MATCH_TYPE", "이상 유저 입니다.");
        exceptionMessages.put("USER_UPDATE_FAILED", "회원정보를 수정하지 못했습니다.");
        exceptionMessages.put("USER_UPDATE_FAILED_DELETE", "회원 삭제에 실패했습니다.");
        exceptionMessages.put("USER_UPDATE_FAILED_UPDATE_TIME", "마지막 로그인 시간을 업데이트 하는데 실패하였습니다. 다시 로그인해주세요.");
        exceptionMessages.put("USER_UPDATE_FAILED_TYPE", "타입을 수정하지 못했습니다. 다시 인증해주세요.");
        exceptionMessages.put("USER_LOGOUT_FAILED", "로그아웃에 실패했습니다.");
        //Email
        exceptionMessages.put("EMAIL_SEND_FAILED", "Email 전송에 실패했습니다.");
        exceptionMessages.put("EMAIL_CACHE_TTL_OUT", "만료시간이 지났습니다.");
        //Category
        exceptionMessages.put("CATEGORY_DUPLICATE_NAME", "중복된 category 입니다.");
        exceptionMessages.put("CATEGORY_ADD_FAILED", "category 등록 오류. 재시도 해주세요.");
        exceptionMessages.put("CATEGORY_UPDATE_FAILED", "category 수정 오류. 재시도 해주세요.");
        exceptionMessages.put("CATEGORY_NOT_MATCH_ID", "해당 카테고리를 찾지 못했습니다.");
        exceptionMessages.put("CATEGORY_NOT_MATCH", "해당 상품을 찾지 못했습니다.");
        exceptionMessages.put("CATEGORY_DELETE_FAILED", "카테고리를 삭제하지 못했습니다.");
        //Product
        exceptionMessages.put("PRODUCT_ADD_FAILED", "상품등록에 실패했습니다. 다시시도해주세요.");
        exceptionMessages.put("PRODUCT_SELECT_FAILED_BY_SALE_ID", "해당 유저의 상품을 찾지 못했습니다.");
        exceptionMessages.put("PRODUCT_UPDATE_FAILED_BY_STATUS", "해당 상품은 경매가 시작되여 수정이 불가능합니다.");
        exceptionMessages.put("PRODUCT_UPDATE_FAILED", "상품을 수정하지 못했습니다.");
        exceptionMessages.put("PRODUCT_UPDATE_FAILED_BY_USER_TYPE", "이상이 있는 유저입니다.(회원 삭제, 이상유저 제재 등의 이유로 삭제 불가)");
        exceptionMessages.put("PRODUCT_UPDATE_FAILED_STATUS", "경매 상태를 바꾸지 못했습니다.");
        exceptionMessages.put("PRODUCT_NOT_MATCH_ID", "해당 상품을 찾지 못했습니다.");
        exceptionMessages.put("PRODUCT_ACCESS_DENIED", "권한이 없어 해당 상품을 수정하지 못합니다.");
        exceptionMessages.put("PRODUCT_ACCESS_DENIED_SELECT", "권한이 없어 해당 상품을 조회하지 못합니다.");
        exceptionMessages.put("PRODUCT_INPUT_MISMATCH_TIME", "경매 시작시간을 잘못 입력하셨습니다. 다시 입력해주세요.");
        exceptionMessages.put("PRODUCT_INPUT_MISMATCH_PRICE", "경매 시작가가 즉시구매가와 같거나 큽니다. 다시 입력해주세요.");
        exceptionMessages.put("PRODUCT_DELETE_FAILED", "상품을 삭제하지 못했습니다.");
        //ProductImage
        exceptionMessages.put("PRODUCT_IMAGE_ADD_FAILED", "이미지 등록에 실패 했습니다.");
        exceptionMessages.put("PRODUCT_IMAGE_DELETE_FAILED", "이미지 삭제에 실패했습니다.");
        //ProductComment
        exceptionMessages.put("PRODUCT_COMMENT_ADD_FAILED", "댓글을 등록하지 못했습니다.");
        //Bid
        exceptionMessages.put("BID_INPUT_MISMATCH", "입력값을 잘못입력했습니다.");
        exceptionMessages.put("BID_ADD_FAILED", "입찰이 되지 않았습니다.");
        exceptionMessages.put("BID_FAILED_NOT_START", "경매가 시작되지 않았습니다.");
        exceptionMessages.put("BID_NULL_DATA", "경매이력이 없습니다.");
        //Payment
        exceptionMessages.put("PAYMENT_NETWORK_CONNECTION_ERROR", "네트워크가 불안정합니다. 확인 후 재시도 해주세요.");
        exceptionMessages.put("PAYMENT_AMOUNT_NOT_MATCH", "요청한 금액과 일치 하지 않습니다.");
        exceptionMessages.put("PAYMENT_REFUNDS_FAILED", "환불에 실패하였습니다. 재시도 해주세요.");
        exceptionMessages.put("PAYMENT_ADD_FAILED", "Payment 추가하지 못했습니다.");
        exceptionMessages.put("PAYMENT_STATUS_FAILED", "결제 상태가 결제 대기가 아닙니다.");
        //ELK
        exceptionMessages.put("ENUM_CONVERTERS_ERROR", "해당하는 productStats enum 값은 없습니다.");
        exceptionMessages.put("BID_NOT_SELECT_BY_PRODUCT_ID", "아직 경매 이력이 없습니다.");
    }

    public static String getExceptionMessage(String exceptionCode) {
        return exceptionMessages.getOrDefault(exceptionCode, "알 수 없는 예외입니다.");
    }
}