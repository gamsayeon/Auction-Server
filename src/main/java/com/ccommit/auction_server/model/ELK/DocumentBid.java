package com.ccommit.auction_server.model.ELK;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;


@Document(indexName = "bid_index")
@Getter
@Setter
@Setting(settingPath = "/static/elastic/elastic-settings.json")
@Mapping(mappingPath = "/static/elastic/product-mappings.json")
public class DocumentBid {
    @Id
    private String id;

    @Field(type = FieldType.Long, name = "bid_id")
    private Long bidId;

    @Field(type = FieldType.Long, name = "buyer_id")
    private Long buyerId;

    @Field(type = FieldType.Long, name = "product_id")
    private Long productId;

    @Field(name = "bid_time", pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS", format = {})
    private LocalDateTime bidTime;

    @Field(type = FieldType.Double, name = "price")
    private int price;

}
