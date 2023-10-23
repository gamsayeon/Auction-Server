package com.ccommit.auction_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "입찰 DTO")
public class BidDTO {
    @Schema(name = "buyerId", description = "구매자 식별자", example = "1")
    private Long buyerId;

    @Schema(name = "productId", description = "상품 식별자", example = "1")
    private Long productId;

    @Schema(name = "bidTime", description = "입찰시간")
    private LocalDateTime bidTime;

    @Min(value = 1000)
    @NotNull
    @Schema(name = "price", description = "입찰 가격", example = "3000")
    private int price;
}