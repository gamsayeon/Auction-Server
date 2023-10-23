package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.enums.UserType;
import com.ccommit.auction_server.model.User;
import com.ccommit.auction_server.util.Sha256Encrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Unit 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private String TEST_USER_ID = "testUserId";
    private String TEST_EMAIL = "test@example.com";
    private String TEST_PASSWORD = Sha256Encrypt.encrypt("testPassword");
    private Long savedUserId;

    @BeforeEach
    public void generateTestUser() {
        //given
        User user = User.builder()
                .userId(TEST_USER_ID)
                .password(TEST_PASSWORD)
                .name("testName")
                .phoneNumber("010-1234-5678")
                .email(TEST_EMAIL)
                .userType(UserType.UNAUTHORIZED_USER)
                .createTime(LocalDateTime.now())
                .build();

        savedUserId = userRepository.save(user).getId();
    }

    @Test
    @DisplayName("유저 식별자로 유저 조회")
    void findById() {
        //when
        User findUser = userRepository.findByUserId("testUserId").orElse(null);

        //then
        assertNotNull(findUser);
        assertEquals(TEST_USER_ID, findUser.getUserId());
    }

    @Test
    @DisplayName("유저 식별자와 업데이트 시간이 널인 유저 조회")
    void findByIdAndUpdateTimeIsNull() {
        //when
        User findUser = userRepository.findByIdAndUpdateTimeIsNull(savedUserId).orElse(null);

        //then
        assertNotNull(findUser);
        assertEquals(savedUserId, findUser.getId());
    }

    @Test
    @DisplayName("유저 ID로 유저 조회")
    void findByUserId() {
        //when
        User findUser = userRepository.findByUserId(TEST_USER_ID).orElse(null);

        //then
        assertNotNull(findUser);
        assertEquals(TEST_USER_ID, findUser.getUserId());
    }

    @Test
    @DisplayName("유저 아이디와 비밀번호로 로그인 확인")
    void findByUserIdAndPassword() {
        //when
        User loginUser = userRepository.findByUserIdAndPassword(TEST_USER_ID, TEST_PASSWORD).orElse(null);

        //then
        assertNotNull(loginUser);
        assertEquals(TEST_USER_ID, loginUser.getUserId());
        assertEquals(TEST_PASSWORD, loginUser.getPassword());
    }

    @Test
    @DisplayName("유효한 유저 Email 확인")
    void existsByEmail() {
        //when
        boolean existsEmail = userRepository.existsByEmail(TEST_EMAIL);

        //then
        assertEquals(true, existsEmail);
    }

    @Test
    @DisplayName("유효한 유저 ID 확인")
    void existsByUserId() {
        //when
        boolean existsUserId = userRepository.existsByUserId(TEST_USER_ID);

        //then
        assertEquals(true, existsUserId);
    }

    @Test
    @DisplayName("유저 식별자로 유저 Email 조회")
    void findEmailById() {
        //when
        String email = userRepository.findUserProjectionById(savedUserId).getEmail();

        //then
        assertNotNull(email);
        assertEquals(TEST_EMAIL, email);
    }
}