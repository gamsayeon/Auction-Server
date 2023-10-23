package com.ccommit.auction_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "상품 댓글 DTO")
public class ProductCommentDTO {
    @Schema(name = "commentId", description = "상품 댓글 식별자(Auto Increment)", example = "2")
    private Long commentId;

    @Schema(name = "parentCommentId", description = "상품 부모 댓글 식별자(Auto Increment)", example = "1")
    private Long parentCommentId;

    @Schema(name = "productId", description = "상품 식별자", example = "1")
    private Long productId;

    @Schema(name = "userId", description = "유저 식별자", example = "1")
    private Long userId;

    @NotBlank
    @Schema(name = "comment", description = "상품 댓글", example = "testComment")
    private String comment;

    @Schema(name = "createTime", description = "상품 댓글 등록 시간")
    private LocalDateTime createTime;
}