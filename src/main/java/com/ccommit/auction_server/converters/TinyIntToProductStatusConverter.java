package com.ccommit.auction_server.converters;

import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.exception.EnumConvertersException;
import org.springframework.core.convert.converter.Converter;

public class TinyIntToProductStatusConverter implements Converter<Integer, ProductStatus> {
    @Override
    public ProductStatus convert(Integer source) {
        switch (source) {
            case 0:
                return ProductStatus.PRODUCT_REGISTRATION;
            case 1:
                return ProductStatus.AUCTION_PROCEEDING;
            case 2:
                return ProductStatus.AUCTION_END;
            case 3:
                return ProductStatus.DELIVERING;
            case 4:
                return ProductStatus.DELIVERY_COMPLETED;
            case 5:
                return ProductStatus.AUCTION_PAUSE;
            default:
                throw new EnumConvertersException("ENUM_CONVERTERS_ERROR", source);
        }
    }
}