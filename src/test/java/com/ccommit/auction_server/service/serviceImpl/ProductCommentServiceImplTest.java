package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.dto.ProductCommentDTO;
import com.ccommit.auction_server.mapper.ProductCommentMapper;
import com.ccommit.auction_server.model.ProductComment;
import com.ccommit.auction_server.repository.ProductCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DisplayName("ProductCommentServiceImpl Unit 테스트")
@ExtendWith(MockitoExtension.class)
class ProductCommentServiceImplTest {
    @InjectMocks
    private ProductCommentServiceImpl productCommentService;
    @Mock
    private ProductCommentRepository productCommentRepository;
    @Mock
    private ProductCommentMapper productCommentMapper;
    private ProductComment convertedBeforeResponseProductComment;
    private ProductCommentDTO requestProductCommentDTO;
    private Long TEST_USER_ID = 1L;
    private Long TEST_PRODUCT_ID = 1L;
    private Long TEST_PRODUCT_COMMENT_ID = 100L;
    private Long TEST_PRODUCT_PARENT_COMMENT_ID = 1L;

    @BeforeEach
    public void generateTestProductComment() {
        convertedBeforeResponseProductComment = ProductComment.builder()
                .commentId(TEST_PRODUCT_COMMENT_ID)
                .parentCommentId(TEST_PRODUCT_PARENT_COMMENT_ID)
                .productId(TEST_PRODUCT_ID)
                .userId(TEST_USER_ID)
                .comment("test comment")
                .build();

        requestProductCommentDTO = ProductCommentDTO.builder()
                .commentId(TEST_PRODUCT_COMMENT_ID)
                .parentCommentId(TEST_PRODUCT_PARENT_COMMENT_ID)
                .productId(TEST_PRODUCT_ID)
                .userId(TEST_USER_ID)
                .comment("test comment")
                .build();
    }

    @Test
    @DisplayName("상품 댓글 등록 성공 테스트")
    void registerProductComment() {
        //given
        when(productCommentMapper.convertToEntity(requestProductCommentDTO)).thenReturn(convertedBeforeResponseProductComment);
        when(productCommentRepository.save(convertedBeforeResponseProductComment)).thenReturn(convertedBeforeResponseProductComment);
        when(productCommentMapper.convertToDTO(convertedBeforeResponseProductComment)).thenReturn(requestProductCommentDTO);

        //when
        ProductCommentDTO result = productCommentService.registerProductComment(TEST_USER_ID, TEST_PRODUCT_ID, requestProductCommentDTO);

        //then
        assertNotNull(result);
        assertEquals(requestProductCommentDTO.getComment(), result.getComment());
    }
}