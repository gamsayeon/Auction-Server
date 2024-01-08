package com.ccommit.auction_server.model.ELK;

import com.ccommit.auction_server.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Document(indexName = "product_index")
@Getter
@Setter
@Setting(settingPath = "/static/elastic/elastic-settings.json")
@Mapping(mappingPath = "/static/elastic/product-mappings.json")
public class DocumentProduct {
    @Id
    private String id;

    @Field(type = FieldType.Long, name = "product_id")
    private Long productId;

    @Field(type = FieldType.Long, name = "sale_id")
    private Long saleId;

    @Field(type = FieldType.Text, name = "product_name")
    private String productName;

    @Field(type = FieldType.Long, name = "category_id")
    private Long categoryId;

    @Field(type = FieldType.Text, name = "explanation")
    private String explanation;

    @Field(name = "product_register_time", pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS", format = {})
    private LocalDateTime productRegisterTime;

    @Field(type = FieldType.Double, name = "start_price")
    private int startPrice;

    @Field(name = "start_time", pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS", format = {})
    private LocalDateTime startTime;

    @Field(name = "end_time", pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS", format = {})
    private LocalDateTime endTime;

    @Field(type = FieldType.Double, name = "highest_price")
    private int highestPrice;

    @Field(name = "product_status")
    private ProductStatus productStatus;

    @Field(type = FieldType.Double, name = "max_bid_price")
    private int maxBidPrice;

    @Field(type = FieldType.Integer, name = "bid_count")
    private int bidCount;
}
