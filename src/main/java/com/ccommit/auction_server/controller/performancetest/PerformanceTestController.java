package com.ccommit.auction_server.controller.performancetest;

import com.ccommit.auction_server.dto.CategoryDTO;
import com.ccommit.auction_server.dto.ProductDTO;
import com.ccommit.auction_server.dto.ProductImageDTO;
import com.ccommit.auction_server.dto.UserDTO;
import com.ccommit.auction_server.model.CommonResponse;
import com.ccommit.auction_server.service.serviceImpl.CategoryServiceImpl;
import com.ccommit.auction_server.service.serviceImpl.PerformanceBidServiceImpl;
import com.ccommit.auction_server.service.serviceImpl.ProductServiceImpl;
import com.ccommit.auction_server.service.serviceImpl.UserServiceImpl;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/performance/test/generated")
@RequiredArgsConstructor
@Profile("performance")
public class PerformanceTestController {
    private final UserServiceImpl userService;
    private final CategoryServiceImpl categoryService;
    private final ProductServiceImpl productService;
    private final PerformanceBidServiceImpl performanceBidService;
    private Faker faker;
    private Set<String> generatedDepartments = new HashSet<>();

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @PostMapping
    @Transactional
    public ResponseEntity<CommonResponse<String>> generatedUsersAndCategory() {

        //성능테스트용 user 20개 랜덤 추가
        for (int i = 1; i <= 20; i++) {
            faker = new Faker();
            String userId = faker.name().username();
            userService.registerUser(UserDTO.builder()
                    .userId(userId)
                    .password(faker.internet().password())
                    .name(faker.name().fullName())
                    .phoneNumber(faker.phoneNumber().phoneNumber())
                    .email(faker.internet().emailAddress())
                    .createTime(LocalDateTime.now())
                    .build());
            userService.updateUserType(userId);
        }

        //성능테스트용 category 50개 랜덤 추가
        for (int i = 1; i <= 50; i++) {
            faker = new Faker();
            String department;
            do {
                department = faker.commerce().department();
            } while (!generatedDepartments.add(department));

            categoryService.registerCategory(CategoryDTO.builder()
                    .categoryName(department)
                    .bidMinPrice(faker.number().numberBetween(1000, 50000))
                    .build());
        }
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "성능테스트용 user 20개 ,category 50개를 추가하였습니다.", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/product")
    @Transactional
    public ResponseEntity<CommonResponse<String>> generatedProducts() {
        //성능테스트용 product 10만개 랜덤 추가
        List<ProductImageDTO> productImageDTOs = new ArrayList<>();

        for (int i = 0; i < 100000; i++) {
            faker = new Faker();
            String generatedProductName = faker.commerce().productName();
            Long generatedSaleId = Double.valueOf(Math.random() * 20 + 1).longValue();
            String generatedExplanation = faker.lorem().sentence();
            Long generatedCategoryId = Double.valueOf(Math.random() * 50 + 1).longValue();
            int generatedStartPrice = faker.number().numberBetween(1000, 100000);

            productService.registerProduct(generatedSaleId, ProductDTO.builder()
                    .saleId(generatedSaleId)
                    .productName(generatedProductName)
                    .categoryId(generatedCategoryId)
                    .explanation(generatedExplanation)
                    .productRegisterTime(LocalDateTime.now())
                    .startPrice(generatedStartPrice)
                    .startTime(LocalDateTime.now().plus(1, ChronoUnit.MINUTES))
                    .endTime(LocalDateTime.now().plus(2, ChronoUnit.YEARS))
                    .highestPrice(Integer.MAX_VALUE)
                    .imageDTOS(productImageDTOs)
                    .build());
        }

        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "성능테스트용 product 10만개를 추가하였습니다.", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bid/history")
    @Transactional
    public ResponseEntity<CommonResponse<String>> generatedBids() {
        performanceBidService.performanceRegisterBid();

        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "성능테스트용 bid 10만개를 추가하였습니다.", null);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/bid")
    @Transactional
    public ResponseEntity<CommonResponse<String>> generatedRegisterBids() {
        performanceBidService.rabbitMQEnqueueBid();

        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "성능테스트용 bid 10만개를 추가하였습니다.", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bid/price/{productId}")
    public ResponseEntity<Integer> generatedSelectBidTopPrice(@RequestParam("productId") Long productId) {
        int price = performanceBidService.selectTopBidPrice(productId);
        return ResponseEntity.ok(price);
    }
}
