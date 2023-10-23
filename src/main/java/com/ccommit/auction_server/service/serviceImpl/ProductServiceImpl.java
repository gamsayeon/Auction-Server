package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.dto.SearchProductDTO;
import com.ccommit.auction_server.enums.ProductSortOrder;
import com.ccommit.auction_server.exception.*;
import com.ccommit.auction_server.mapper.ProductImageMapper;
import com.ccommit.auction_server.mapper.ProductMapper;
import com.ccommit.auction_server.model.ProductImage;
import com.ccommit.auction_server.projection.UserProjection;
import com.ccommit.auction_server.repository.*;
import com.ccommit.auction_server.service.ProductService;
import com.ccommit.auction_server.dto.ProductDTO;
import com.ccommit.auction_server.dto.ProductImageDTO;
import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.exception.*;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final int TIME_COMPARE = 0;
    private final int DELETE_FAIL = 0;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;
    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    @Override
    @Transactional
    public ProductDTO registerProduct(Long saleId, ProductDTO productDTO) {
        this.validatorProduct(productDTO);
        Product product = productMapper.convertToEntity(productDTO);
        product.setSaleId(saleId);
        product.setProductRegisterTime(LocalDateTime.now());
        product.setProductStatus(ProductStatus.PRODUCT_REGISTRATION);
        Product resultProduct = productRepository.save(product);
        if (resultProduct != null) {
            ProductDTO resultProductDTO = productMapper.convertToDTO(resultProduct);
            try {
                if (!productDTO.getImageDTOS().isEmpty()) {
                    List<ProductImageDTO> resultProductImages =
                            this.registerProductImage(productDTO, resultProduct.getProductId());
                    resultProductDTO.setImageDTOS(resultProductImages);
                }
            } catch (AddFailedException e) {
                logger.warn("이미지 등록에 실패 했습니다.");
            }
            logger.info("상품을 정상적으로 등록했습니다.");
            return resultProductDTO;
        } else {
            logger.warn("상품등록에 실패했습니다. 다시시도해주세요.");
            throw new AddFailedException("PRODUCT_ADD_FAILED", product);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<ProductImageDTO> registerProductImage(ProductDTO productDTO, Long productId) {
        List<ProductImage> resultProductImages = new ArrayList<>();
        List<ProductImage> productImages = productImageMapper.convertToEntity(productDTO);
        for (ProductImage productImage : productImages) {
            if (productImage.getImagePath() != "") {
                productImage.setProductId(productId);
                ProductImage resultProductImage = productImageRepository.save(productImage);
                if (resultProductImage == null) {
                    logger.warn("이미지 등록에 실패 했습니다.");
                    throw new AddFailedException("PRODUCT_IMAGE_ADD_FAILED", productImage);
                } else resultProductImages.add(resultProductImage);
            } else {
                logger.warn("이미지 등록에 실패 했습니다.");
                throw new AddFailedException("PRODUCT_IMAGE_ADD_FAILED", productImage);
            }
        }
        return productImageMapper.convertToDTO(resultProductImages);
    }

    public void validatorProduct(ProductDTO productDTO) {
        if (!categoryRepository.existsByCategoryId(productDTO.getCategoryId())) {
            logger.warn("해당 카테고리를 찾지 못했습니다.");
            throw new NotMatchingException("CATEGORY_NOT_MATCH_ID", productDTO.getCategoryId());
        }
        if (productDTO.getStartTime().compareTo(LocalDateTime.now()) < TIME_COMPARE) {  //경매 시작시간을 과거로 입력
            logger.warn("경매 시작시간을 과거 시간으로 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputMismatchException("PRODUCT_INPUT_MISMATCH_TIME", productDTO);
        } else if (productDTO.getEndTime().compareTo(LocalDateTime.now()) < TIME_COMPARE) { //경매 마감시간을 과거로 입력
            logger.warn("경매 마감시간을 과거 시간으로 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputMismatchException("PRODUCT_INPUT_MISMATCH_TIME", productDTO);
        } else if (productDTO.getStartTime().compareTo(productDTO.getEndTime()) > TIME_COMPARE) {   //경매 마감시간을 경매 시작시간보다 과거로 입력
            logger.warn("경매 시작시간을 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputMismatchException("PRODUCT_INPUT_MISMATCH_TIME", productDTO);
        } else if (productDTO.getStartPrice() >= productDTO.getHighestPrice()) {    // 경매 시작가가 즉시구매가보다 작거나 같을때
            logger.warn("경매 시작가가 즉시구매가와 같거나 큽니다. 다시 입력해주세요.");
            throw new InputMismatchException("PRODUCT_INPUT_MISMATCH_PRICE", productDTO);
        }
    }

    @Override
    public ProductDTO selectProduct(Long productId) {
        Product product = productRepository.findByProductId(productId);
        if (product != null) {
            switch (product.getProductStatus()) {
                case PRODUCT_REGISTRATION:
                case AUCTION_PROCEEDING:
                    ProductDTO resultProductDTO = productMapper.convertToDTO(product);
                    List<ProductImage> productImages = productImageRepository.findByProductId(productId);
                    if (!productImages.isEmpty()) {
                        List<ProductImageDTO> resultProductImageDTOs = productImageMapper.convertToDTO(productImages);
                        resultProductDTO.setImageDTOS(resultProductImageDTOs);
                        logger.info("이미지를 정상적으로 조회했습니다.");
                    }
                    logger.info("상품을 정상적으로 조회했습니다.");
                    return resultProductDTO;
                default:
                    logger.warn("상품의 조회 권한이 없습니다.");
                    throw new UserAccessDeniedException("PRODUCT_ACCESS_DENIED_SELECT", productId);
            }
        } else {
            logger.warn("해당 상품을 찾지 못했습니다.");
            throw new NotMatchingException("PRODUCT_NOT_MATCH_ID", productId);
        }
    }

    @Override
    public List<ProductDTO> selectProductForUser(Long saleId) {
        List<Product> products = productRepository.findBySaleId(saleId);
        if (!products.isEmpty()) {
            List<ProductDTO> resultProductDTOs = new ArrayList<>();
            for (Product product : products) {
                ProductDTO resultProductDTO = productMapper.convertToDTO(product);
                List<ProductImage> productImages = productImageRepository.findByProductId(product.getProductId());
                if (!productImages.isEmpty()) {
                    List<ProductImageDTO> resultProductImageDTOs = productImageMapper.convertToDTO(productImages);
                    resultProductDTO.setImageDTOS(resultProductImageDTOs);
                    logger.info("이미지를 정상적으로 조회했습니다.");
                }
                resultProductDTOs.add(resultProductDTO);
            }
            logger.info("상품을 정상적으로 조회했습니다.");
            return resultProductDTOs;
        } else {
            logger.warn("해당 유저의 상품을 찾지 못했습니다.");
            throw new NotMatchingException("PRODUCT_SELECT_FAILED_BY_SALE_ID", saleId);
        }
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long saleId, Long productId, ProductDTO productDTO) {
        Product resultProduct = productRepository.findByProductId(productId);
        if (resultProduct.getProductStatus() != ProductStatus.PRODUCT_REGISTRATION) {
            logger.warn("해당 상품은 경매가 시작되여 수정이 불가능합니다.");
            throw new UpdateFailedException("PRODUCT_UPDATE_FAILED_BY_STATUS", resultProduct.getProductStatus());
        }
        if (resultProduct.getSaleId() == saleId) {
            this.validatorProduct(productDTO);
            Product product = productMapper.convertToEntity(productDTO);
            product.setProductId(resultProduct.getProductId());
            product.setSaleId(resultProduct.getSaleId());
            product.setProductRegisterTime(resultProduct.getProductRegisterTime());
            resultProduct = productRepository.save(resultProduct);
            if (resultProduct == null) {
                logger.warn("상품을 수정하지 못했습니다.");
                throw new UpdateFailedException("PRODUCT_UPDATE_FAILED", "retry");
            }
            ProductDTO resultProductDTO = productMapper.convertToDTO(resultProduct);
            this.deleteProductImage(productId);
            List<ProductImageDTO> resultProductImageDTOs = this.registerProductImage(productDTO, productId);
            resultProductDTO.setImageDTOS(resultProductImageDTOs);
            logger.info("상품을 수정했습니다.");
            return resultProductDTO;
        } else {
            logger.warn("권한이 없어 해당 상품을 수정하지 못합니다.");
            throw new UserAccessDeniedException("PRODUCT_ACCESS_DENIED", saleId);
        }
    }

    @Override
    @Transactional
    public void updateProductStatus(ProductStatus productStatus) {
        List<Product> resultProducts = productRepository.findByProductStatus(ProductStatus.PRODUCT_REGISTRATION, ProductStatus.AUCTION_PROCEEDING);
        for (Product product : resultProducts) {
            switch (product.getProductStatus()) {
                case PRODUCT_REGISTRATION:
                    if (product.getStartTime().compareTo(LocalDateTime.now()) <= 0) { //경매 시작시간이 현재시간과 비교해서 과거인지 확인
                        product.setProductStatus(ProductStatus.AUCTION_PROCEEDING);
                        Product resultProduct = productRepository.save(product);
                        if (resultProduct == null) {
                            logger.warn("경매 상태를 수정하지 못했습니다.");
                            throw new UpdateFailedException("PRODUCT_UPDATE_FAILED_STATUS", product.getProductId());
                        } else {
                            logger.info("경매 상태를 성공적으로 변경했습니다.");
                            UserProjection recipientEmail = userRepository.findUserProjectionById(resultProduct.getSaleId());
                            emailService.notifyAuction(recipientEmail.getEmail(), resultProduct.getProductStatus().toString(),
                                    resultProduct.getProductName() + "의 경매상태가 변경되었습니다.");
                        }
                    }
                    break;
                case AUCTION_PROCEEDING:
                    if (product.getEndTime().compareTo(LocalDateTime.now()) <= 0) {   //경매 마감시간이 현재시간과 비교해서 과거인지 확인
                        product.setProductStatus(ProductStatus.AUCTION_END);
                        Product resultProduct = productRepository.save(product);
                        if (resultProduct == null) {
                            logger.warn("경매 상태를 수정하지 못했습니다.");
                            throw new UpdateFailedException("PRODUCT_UPDATE_FAILED_STATUS", product.getProductId());
                        } else {
                            logger.info("경매 상태를 성공적으로 변경했습니다.");
                            UserProjection recipientEmail = userRepository.findUserProjectionById(resultProduct.getSaleId());
                            emailService.notifyAuction(recipientEmail.getEmail(), resultProduct.getProductStatus().toString(),
                                    resultProduct.getProductName() + "의 경매상태가 변경되었습니다.");
                        }
                    }
                    break;
            }
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long saleId, Long productId) {
        Product resultProduct = productRepository.findByProductId(productId);
        if (resultProduct.getProductStatus() != ProductStatus.PRODUCT_REGISTRATION) {
            logger.warn("해당 상품은 경매가 시작되어 삭제가 불가능합니다.");
            throw new UpdateFailedException("PRODUCT_UPDATE_FAILED_BY_STATUS", resultProduct.getProductStatus());
        } else if (saleId == resultProduct.getSaleId()) {
            this.deleteProductImage(productId);
            if (productRepository.deleteByProductId(productId) == DELETE_FAIL) {
                logger.warn("상품을 삭제 하지 못했습니다.");
                throw new DeleteFailedException("PRODUCT_DELETE_FAILED", productId);
            } else {
                logger.info("상품을 삭제 했습니다.");
            }
        } else {
            logger.warn("권한이 없습니다.");
            throw new UserAccessDeniedException("COMMON_ACCESS_DENIED", saleId);
        }
    }

    public void deleteProductImage(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);
        if (!productImages.isEmpty()) {
            if (productImageRepository.deleteAllByProductId(productId) != productImages.size()) {
                logger.warn("이미지를 삭제 하지 못했습니다.");
                throw new DeleteFailedException("PRODUCT_IMAGE_DELETE_FAILED", productId);
            } else {
                logger.info("이미지를 정상적으로 삭제했습니다.");
            }
        }
    }

    @Override
    public SearchProductDTO findByKeyword(String productName, Long saleId, Long categoryId,
                                          String explanation, int page, int pageSize, ProductSortOrder sortOrder) {
        List<Product> products = productRepository.searchProducts(productName, saleId, categoryId, explanation, page, pageSize);
        products = this.sortProducts(sortOrder, products);

        List<ProductDTO> productDTOs = new ArrayList<>();
        for (Product product : products) {
            productDTOs.add(productMapper.convertToDTO(product));
        }
        int totalItems = products.size();
        int totalPages = totalItems / pageSize;
        if (totalItems % pageSize != 0) {
            totalPages++;
        }
        SearchProductDTO searchProductDTD = SearchProductDTO.builder()
                .page(page)
                .pageSize(pageSize)
                .totalItems(totalItems)
                .totalPages(totalPages)
                .productDTOs(productDTOs)
                .build();
        return searchProductDTD;
    }

    public List<Product> sortProducts(ProductSortOrder sortOrder, List<Product> products) {
        switch (sortOrder) {
            case BIDDER_COUNT_DESC:             //입찰자가 많은 순
                Collections.sort(products, (p1, p2) -> {
                    Long productId1 = p1.getProductId();
                    Long productId2 = p2.getProductId();

                    if (bidRepository.countByProductId(productId1) > bidRepository.countByProductId(productId2)) {
                        return -1; // p1이 p2보다 작으면 음수 값 반환
                    } else if (bidRepository.countByProductId(productId1) < bidRepository.countByProductId(productId2)) {
                        return 1; // p1이 p2보다 크면 양수 값 반환
                    } else {
                        return 0; // p1과 p2가 같으면 0 반환
                    }
                });
                break;
            case HIGHEST_PRICE_DESC:     // 최고 즉시 구매가 순
                Collections.sort(products, (p1, p2) -> {
                    double highestPrice1 = p1.getHighestPrice();
                    double highestPrice2 = p2.getHighestPrice();
                    if (highestPrice1 < highestPrice2) {
                        return -1; // p1이 p2보다 작으면 음수 값 반환
                    } else if (highestPrice1 > highestPrice2) {
                        return 1; // p1이 p2보다 크면 양수 값 반환
                    } else {
                        return 0; // p1과 p2가 같으면 0 반환
                    }
                });
                break;
            case HIGHEST_PRICE_ASC:     // 최저 즉시 구매가 순
                Collections.sort(products, (p1, p2) -> {
                    double highestPrice1 = p1.getHighestPrice();
                    double highestPrice2 = p2.getHighestPrice();
                    if (highestPrice1 < highestPrice2) {
                        return 1; // 역순: p1이 p2보다 작으면 양수 값 반환
                    } else if (highestPrice1 > highestPrice2) {
                        return -1; // 역순: p1이 p2보다 크면 음수 값 반환
                    } else {
                        return 0; // p1과 p2가 같으면 0 반환
                    }
                });
                break;
            case BID_PRICE_DESC:             //최고 입찰가 순
                Collections.sort(products, (p1, p2) -> {
                    Integer maxPriceProductId1 = this.currentBid(p1.getProductId());
                    Integer maxPriceProductId2 = this.currentBid(p2.getProductId());

                    if (maxPriceProductId1 > maxPriceProductId2) {
                        return -1; // p1이 p2보다 작으면 음수 값 반환
                    } else if (maxPriceProductId1 < maxPriceProductId2) {
                        return 1; // p1이 p2보다 크면 양수 값 반환
                    } else {
                        return 0; // p1과 p2가 같으면 0 반환
                    }
                });
                break;
            case BID_PRICE_ASC:             //최저 입찰가 순
                Collections.sort(products, (p1, p2) -> {
                    Integer maxPriceProductId1 = this.currentBid(p1.getProductId());
                    Integer maxPriceProductId2 = this.currentBid(p2.getProductId());

                    if (maxPriceProductId1 < maxPriceProductId2) {
                        return -1; // p1이 p2보다 작으면 음수 값 반환
                    } else if (maxPriceProductId1 > maxPriceProductId2) {
                        return 1; // p1이 p2보다 크면 양수 값 반환
                    } else {
                        return 0; // p1과 p2가 같으면 0 반환
                    }
                });
                break;
            case NEWEST_FIRST:                  //등록일 최신 순
                Collections.sort(products, (p1, p2) -> {
                    LocalDateTime registerTime1 = p1.getProductRegisterTime();
                    LocalDateTime registerTime2 = p2.getProductRegisterTime();
                    return registerTime1.compareTo(registerTime2);
                });
                break;
            case OLDEST_FIRST:                  //등록일 과거 순
                Collections.sort(products, (p1, p2) -> {
                    LocalDateTime registerTime1 = p1.getProductRegisterTime();
                    LocalDateTime registerTime2 = p2.getProductRegisterTime();
                    return registerTime2.compareTo(registerTime1);
                });
                break;
        }
        return products;
    }

    public Integer currentBid(Long productId) {
        Integer maxPriceProductId = bidRepository.findMaxPriceByProductId(productId);
        if (maxPriceProductId == null)
            maxPriceProductId = productRepository.findByProductId(productId).getStartPrice();
        return maxPriceProductId;
    }

}