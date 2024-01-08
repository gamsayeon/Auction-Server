package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.dto.ProductDTO;
import com.ccommit.auction_server.dto.ProductImageDTO;
import com.ccommit.auction_server.elasticsearchRepository.ProductSearchRepository;
import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.mapper.ProductImageMapper;
import com.ccommit.auction_server.mapper.ProductMapper;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.model.ProductImage;
import com.ccommit.auction_server.projection.UserProjection;
import com.ccommit.auction_server.repository.*;
import com.ccommit.auction_server.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DisplayName("ProductServiceImpl Unit 테스트")
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private EmailService emailService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductSearchRepository productSearchRepository;
    @Mock
    private BidRepository bidRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductImageMapper productImageMapper;
    private Product convertedBeforeResponseProduct;
    private ProductDTO requestProductDTO;
    private List<ProductImageDTO> requestProductImageDTO;
    private List<ProductImage> convertedBeforeResponseProductImage;
    private Long TEST_SALE_ID = 1L;
    private Long TEST_CATEGORY_ID = 1L;
    private Long TEST_PRODUCT_ID = 1L;
    private final int DELETE_SUCCESS = 1;

    @BeforeEach
    public void generateTestProduct() {
        convertedBeforeResponseProduct = Product.builder()
                .productId(TEST_PRODUCT_ID)
                .saleId(TEST_SALE_ID)
                .productName("test Product Name")
                .categoryId(TEST_CATEGORY_ID)
                .explanation("test Explanation")
                .productRegisterTime(LocalDateTime.now())
                .startPrice(1000)
                .startTime(LocalDateTime.now().plus(30, ChronoUnit.MINUTES))
                .endTime(LocalDateTime.now().plus(1, ChronoUnit.HOURS))
                .highestPrice(100000)
                .productStatus(ProductStatus.PRODUCT_REGISTRATION)
                .build();

        requestProductImageDTO = List.of(ProductImageDTO.builder()
                .imageId(1L)
                .productId(TEST_PRODUCT_ID)
                .imagePath("/testPath")
                .build());

        convertedBeforeResponseProductImage = List.of(ProductImage.builder()
                .imageId(1L)
                .productId(TEST_PRODUCT_ID)
                .imagePath("/testPath")
                .build());

        requestProductDTO = ProductDTO.builder()
                .saleId(TEST_SALE_ID)
                .productName("test Product Name")
                .categoryId(TEST_CATEGORY_ID)
                .explanation("test Explanation")
                .productRegisterTime(LocalDateTime.now())
                .startPrice(1000)
                .startTime(LocalDateTime.now().plus(30, ChronoUnit.MINUTES))
                .endTime(LocalDateTime.now().plus(1, ChronoUnit.HOURS))
                .highestPrice(100000)
                .productStatus(ProductStatus.PRODUCT_REGISTRATION)
                .imageDTOS(requestProductImageDTO)
                .build();
    }

    void validatorProduct() {
        //given
        when(categoryRepository.existsByCategoryId(TEST_CATEGORY_ID)).thenReturn(true);

        //when, then
        assertDoesNotThrow(() -> productService.validatorProduct(requestProductDTO));
    }

    @Test
    @DisplayName("상품 이미지 등록 성공 테스트")
    void registerProductImage() {
        //given
        when(productImageMapper.convertToEntity(requestProductDTO, TEST_PRODUCT_ID)).thenReturn(convertedBeforeResponseProductImage);
        for (ProductImage productImage : convertedBeforeResponseProductImage) {
            when(productImageRepository.save(productImage)).thenReturn(productImage);
        }
        when(productImageMapper.convertToDTO(convertedBeforeResponseProductImage)).thenReturn(requestProductImageDTO);

        //when
        List<ProductImageDTO> result = productService.registerProductImage(requestProductDTO, TEST_PRODUCT_ID);

        //then
        for (int i = 0; i < result.size(); i++) {
            assertEquals(requestProductImageDTO.get(i).getImagePath(), result.get(i).getImagePath());
        }
    }

    @Test
    @DisplayName("상품 등록 성공 테스트")
    void registerProduct() {
        //given
        this.validatorProduct();
        when(productMapper.convertToEntity(requestProductDTO)).thenReturn(convertedBeforeResponseProduct);
        when(productRepository.save(convertedBeforeResponseProduct)).thenReturn(convertedBeforeResponseProduct);
        when(productMapper.convertToDTO(convertedBeforeResponseProduct)).thenReturn(requestProductDTO);
        this.registerProductImage();

        //when
        ProductDTO result = productService.registerProduct(TEST_SALE_ID, requestProductDTO);

        //then
        assertEquals(requestProductDTO.getProductName(), result.getProductName());
    }

    @Test
    @DisplayName("상품 식별자로 조회 성공 테스트")
    void selectProduct() {
        //given
        when(productRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(convertedBeforeResponseProduct);
        when(productMapper.convertToDTO(convertedBeforeResponseProduct)).thenReturn(requestProductDTO);
        when(productImageRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(convertedBeforeResponseProductImage);
        when(productImageMapper.convertToDTO(convertedBeforeResponseProductImage)).thenReturn(requestProductImageDTO);

        //when
        ProductDTO result = productService.selectProduct(TEST_PRODUCT_ID);

        //then
        assertEquals(requestProductDTO.getProductName(), result.getProductName());
        assertEquals(requestProductDTO.getSaleId(), result.getSaleId());
        for (int i = 0; i < result.getImageDTOS().size(); i++) {
            assertEquals(requestProductImageDTO.get(i).getImagePath(), result.getImageDTOS().get(i).getImagePath());
        }
    }

    @Test
    @DisplayName("유저 식별자 조회 성공 테스트")
    void selectProductForUser() {
        //given
        when(productRepository.findBySaleId(TEST_SALE_ID)).thenReturn(List.of(convertedBeforeResponseProduct));
        when(productMapper.convertToDTO(convertedBeforeResponseProduct)).thenReturn(requestProductDTO);
        when(productImageRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(convertedBeforeResponseProductImage);
        when(productImageMapper.convertToDTO(convertedBeforeResponseProductImage)).thenReturn(requestProductImageDTO);

        //when
        List<ProductDTO> result = productService.selectProductForUser(TEST_SALE_ID);

        //then
        for (int i = 0; i < result.size(); i++) {
            assertEquals(requestProductDTO.getProductName(), result.get(i).getProductName());
            assertEquals(requestProductDTO.getSaleId(), result.get(i).getSaleId());
            for (ProductImageDTO productImageDTO : result.get(i).getImageDTOS()) {
                assertEquals(requestProductImageDTO.get(i).getImagePath(), productImageDTO.getImagePath());
            }
        }
    }

    @Test
    @DisplayName("상품 이미지 삭제 성공 테스트")
    void deleteProductImage() {
        //given
        when(productImageRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(convertedBeforeResponseProductImage);
        when(productImageRepository.deleteAllByProductId(TEST_PRODUCT_ID)).thenReturn(convertedBeforeResponseProductImage.size());

        //when, then
        assertDoesNotThrow(() -> productService.deleteProductImage(TEST_PRODUCT_ID));
    }

    @Test
    @DisplayName("상품 수정 성공 테스트")
    void updateProduct() {
        //given
        when(productRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(convertedBeforeResponseProduct);
        requestProductDTO.setProductName("testUpdateProductName");
        this.validatorProduct();
        convertedBeforeResponseProduct.setProductName("testUpdateProductName");
        when(productMapper.convertToEntity(requestProductDTO)).thenReturn(convertedBeforeResponseProduct);
        when(productRepository.save(convertedBeforeResponseProduct)).thenReturn(convertedBeforeResponseProduct);
        when(productMapper.convertToDTO(convertedBeforeResponseProduct)).thenReturn(requestProductDTO);
        this.deleteProductImage();
        this.registerProductImage();

        //when
        ProductDTO result = productService.updateProduct(TEST_SALE_ID, TEST_PRODUCT_ID, requestProductDTO);

        //then
        assertEquals(requestProductDTO.getProductName(), result.getProductName());
    }

    @Test
    @DisplayName("상품 상태 변경 성공 테스트 - 상품 등록에서 경매 진행 상태로 수정")
    void updateProductStatus() {
        //given
        convertedBeforeResponseProduct.setStartTime(LocalDateTime.now());
        when(productRepository.findByProductStatus(ProductStatus.PRODUCT_REGISTRATION, ProductStatus.AUCTION_PROCEEDING))
                .thenReturn(List.of(convertedBeforeResponseProduct));
        Product updateProduct = Product.builder()
                .productId(TEST_PRODUCT_ID)
                .saleId(TEST_SALE_ID)
                .productName("test Product Name")
                .categoryId(TEST_CATEGORY_ID)
                .explanation("test Explanation")
                .productRegisterTime(LocalDateTime.now())
                .startPrice(1000)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plus(1, ChronoUnit.HOURS))
                .highestPrice(100000)
                .productStatus(ProductStatus.AUCTION_PROCEEDING)
                .build();
        when(productRepository.save(convertedBeforeResponseProduct)).thenReturn(updateProduct);
        UserProjection resultUserProjection = () -> {
            return "test@example.com"; // 원하는 이메일 주소로 설정
        };
        when(userRepository.findUserProjectionById(TEST_SALE_ID)).thenReturn(resultUserProjection);

        //when, then
        assertDoesNotThrow(() -> productService.updateProductStatus());
    }

    @Test
    @DisplayName("상품 삭제 성공 테스트")
    void deleteProduct() {
        //given
        when(productRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(convertedBeforeResponseProduct);
        when(productImageRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(convertedBeforeResponseProductImage);
        when(productImageRepository.deleteAllByProductId(TEST_PRODUCT_ID)).thenReturn(convertedBeforeResponseProductImage.size());
        when(productRepository.deleteByProductId(TEST_PRODUCT_ID)).thenReturn(DELETE_SUCCESS);

        //when, then
        assertDoesNotThrow(() -> productService.deleteProduct(TEST_SALE_ID, TEST_PRODUCT_ID));
        assertDoesNotThrow(() -> productService.deleteProductImage(TEST_PRODUCT_ID));
    }

//      TODO : elasticsearch 로 test code 수정중
//    @Test
//    @DisplayName("상품 검색 성공 테스트 - postName 으로 검색")
//    void findByKeyword() {
//        //given
//        String searchPostName = "test";
//        List<Product> resultSearchProduct = new ArrayList<>();
//        resultSearchProduct.add(convertedBeforeResponseProduct);
//        resultSearchProduct.add(convertedBeforeResponseProduct);
//        when(productMapper.convertToDTO(convertedBeforeResponseProduct)).thenReturn(requestProductDTO);
//        when(productSearchRepositoryElk.searchProducts(searchPostName, null, null, null))
//                .thenReturn(resultSearchProduct);
//        this.sortProducts();
//
//        //when
//        SearchProductDTO result = productService.findByKeyword(searchPostName,
//                null, null, null, 1, 10, ProductSortOrder.BIDDER_COUNT_DESC);
//
//        //then
//        for (ProductDTO productDTO : result.getProductDTOs()) {
//            assertTrue(productDTO.getProductName().contains(searchPostName));
//        }
//    }
//
//    @Test
//    @DisplayName("상품 정렬 성공 테스트 - BIDDER_COUNT_DESC(입찰자 많은순으로 내림차순)으로 검색")
//    void sortProducts() {
//        //given
//        Long TEST_PRODUCT_ID2 = 2L;
//        List<Product> resultSearchProduct = new ArrayList<>();
//        resultSearchProduct.add(convertedBeforeResponseProduct);
//        Product sortExampleProduct = Product.builder().productId(TEST_PRODUCT_ID2).build();
//        resultSearchProduct.add(sortExampleProduct);
//        lenient().when(bidRepository.countByProductId(TEST_PRODUCT_ID)).thenReturn(1L);    //상품의 입찰 수가 1이라고 가정
//        lenient().when(bidRepository.countByProductId(TEST_PRODUCT_ID2)).thenReturn(5L);    //상품의 입찰 수가 5이라고 가정
//
//        //when
//        List<Product> result = productService.sortProducts(ProductSortOrder.BIDDER_COUNT_DESC, resultSearchProduct);
//
//        //then
//        for (int i = 0; i < result.size(); i++) {
//            assertEquals(resultSearchProduct.get(i).getProductId(), result.get(i).getProductId());
//        }
//    }

    @Test
    @DisplayName("현재 상품의 입찰 최고가 검색")
    void currentBid() {
        //given
        when(bidRepository.findTopByProductIdOrderByPriceDesc(TEST_PRODUCT_ID)).thenReturn(null);
        when(productRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(convertedBeforeResponseProduct);

        //when
        Integer result = productService.currentBid(TEST_PRODUCT_ID);

        //then
        assertEquals(convertedBeforeResponseProduct.getStartPrice(), result);
    }
}