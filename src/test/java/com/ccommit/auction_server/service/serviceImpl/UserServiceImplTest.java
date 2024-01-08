package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.dto.UserDTO;
import com.ccommit.auction_server.enums.UserType;
import com.ccommit.auction_server.exception.LogoutFailedException;
import com.ccommit.auction_server.exception.NotMatchingException;
import com.ccommit.auction_server.exception.UpdateFailedException;
import com.ccommit.auction_server.mapper.UserMapper;
import com.ccommit.auction_server.model.User;
import com.ccommit.auction_server.repository.UserRepository;
import com.ccommit.auction_server.util.Sha256Encrypt;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@DisplayName("UserServiceImpl Unit 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private HttpSession session;
    private String TEST_USER_ID = "testUserId";
    private String TEST_EMAIL = "test@example.com";
    private String TEST_PASSWORD = "testPassword";
    private String TEST_ENCRYPT_PASSWORD = Sha256Encrypt.encrypt(TEST_PASSWORD);
    private UserDTO requestUserDTO;
    private User convertedBeforeResponseUser;

    @BeforeEach
    public void generateTestUser() {
        requestUserDTO = UserDTO.builder()
                .userId(TEST_USER_ID)
                .password(TEST_PASSWORD)
                .name("testName")
                .phoneNumber("010-1234-5678")
                .email(TEST_EMAIL)
                .userType(UserType.UNAUTHORIZED_USER)
                .createTime(LocalDateTime.now())
                .build();

        convertedBeforeResponseUser = User.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .password(TEST_ENCRYPT_PASSWORD)
                .name("testName")
                .phoneNumber("010-1234-5678")
                .email(TEST_EMAIL)
                .userType(UserType.UNAUTHORIZED_USER)
                .createTime(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("유저 등록 성공 테스트")
    void registerUser() {
        //given
        when(userMapper.convertToEntity(requestUserDTO)).thenReturn(convertedBeforeResponseUser);
        when(userRepository.save(convertedBeforeResponseUser)).thenReturn(convertedBeforeResponseUser);
        when(userMapper.convertToDTO(convertedBeforeResponseUser)).thenReturn(requestUserDTO);

        //when
        UserDTO result = userService.registerUser(requestUserDTO);

        //then
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getUserId());
        Assertions.assertEquals(UserType.UNAUTHORIZED_USER, result.getUserType());
    }

    @Test
    @DisplayName("유저 타입 변경 성공 테스트")
    void updateUserType() {
        //given
        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(convertedBeforeResponseUser));
        when(userRepository.save(convertedBeforeResponseUser)).thenReturn(convertedBeforeResponseUser);
        when(userMapper.convertToDTO(convertedBeforeResponseUser)).thenAnswer(invocation -> {
            requestUserDTO.setUserType(convertedBeforeResponseUser.getUserType());
            return requestUserDTO;
        });

        //when
        UserDTO result = userService.updateUserType(TEST_USER_ID);

        //then
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getUserId());
        Assertions.assertEquals(UserType.USER, result.getUserType());
    }

    @Test
    @DisplayName("유저 타입 변경 실패 테스트 - 해당하는 user_id가 없을 때")
    void updateUserTypeFailWhenUserNotFound() {
        //given
        when(userRepository.findByUserId(any())).thenReturn(Optional.empty());

        //when, then
        assertThrows(NotMatchingException.class, () -> userService.updateUserType(any()));
    }

    @Test
    @DisplayName("유저 타입 변경 실패 테스트 - DB connection Error")
    void updateUserTypeFailWhenDBError() {
        //given
        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(convertedBeforeResponseUser));
        when(userRepository.save(convertedBeforeResponseUser)).thenReturn(null);

        //when, then
        assertThrows(UpdateFailedException.class, () -> userService.updateUserType(TEST_USER_ID));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginUser() {
        //given
        when(userMapper.convertToEntity(requestUserDTO)).thenReturn(convertedBeforeResponseUser);
        when(userRepository.findByUserIdAndPassword(convertedBeforeResponseUser.getUserId(), convertedBeforeResponseUser.getPassword()))
                .thenReturn(Optional.of(convertedBeforeResponseUser));
        when(userRepository.save(convertedBeforeResponseUser)).thenReturn(convertedBeforeResponseUser);
        when(userMapper.convertToDTO(convertedBeforeResponseUser)).thenReturn(requestUserDTO);

        //when
        UserDTO result = userService.loginUser(requestUserDTO, session);

        //then
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getUserId());

        verify(session).setAttribute(ArgumentMatchers.eq(requestUserDTO.getUserType().toString()), eq(convertedBeforeResponseUser.getId()));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 존재하지 않은 user_id 이거나 wrong password 시")
    void loginUserFailedWhenNotFoundUser() {
        //given
        UserDTO wrongUserDTO = new UserDTO();
        wrongUserDTO.setUserId("nonexistentUser");
        wrongUserDTO.setPassword("wrongPassword");
        when(userMapper.convertToEntity(wrongUserDTO)).thenReturn(new User());
        when(userRepository.findByUserIdAndPassword(any(), any())).thenReturn(Optional.empty());

        //when, then
        assertThrows(NotMatchingException.class, () -> userService.loginUser(wrongUserDTO, session));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 이상 유저가 로그인할 경우")
    void loginUserFailedWhenTypeIsActivity() {
        //given
        when(userMapper.convertToEntity(requestUserDTO)).thenAnswer(invocation -> {
            convertedBeforeResponseUser.setUserType(UserType.STOP_ACTIVITY);
            return convertedBeforeResponseUser;
        });
        when(userRepository.findByUserIdAndPassword(convertedBeforeResponseUser.getUserId(), convertedBeforeResponseUser.getPassword())).thenReturn(Optional.of(convertedBeforeResponseUser));

        //when, then
        assertThrows(NotMatchingException.class, () -> userService.loginUser(requestUserDTO, session));
    }

    @Test
    @DisplayName("유저 조회 성공 테스트")
    void selectUser() {
        //given
        when(userRepository.findByIdAndUpdateTimeIsNull(convertedBeforeResponseUser.getId())).thenReturn(Optional.of(convertedBeforeResponseUser));
        when(userMapper.convertToDTO(convertedBeforeResponseUser)).thenReturn(requestUserDTO);

        //when
        UserDTO result = userService.selectUser(convertedBeforeResponseUser.getId());

        //then
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getUserId());
    }

    @Test
    @DisplayName("유저 조회 실패 테스트 - 존재 하지 않은 유저 식별자이거나 update_time 이 null 이 아닌 경우")
    void selectUserFailedWhenNotFoundUser() {
        //given
        when(userRepository.findByIdAndUpdateTimeIsNull(any())).thenReturn(Optional.empty());

        //when, then
        assertThrows(NotMatchingException.class, () -> userService.selectUser(any()));
    }

    @Test
    @DisplayName("유저 업데이트 성공 테스트")
    void updateUser() {
        //given
        User updateUser = User.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .password(TEST_ENCRYPT_PASSWORD)
                .name("testUpdateName")         //
                .phoneNumber("010-4321-8765")
                .email(TEST_EMAIL)
                .userType(UserType.UNAUTHORIZED_USER)
                .createTime(LocalDateTime.now())
                .build();

        requestUserDTO.setName("testUpdateName");
        requestUserDTO.setPhoneNumber("010-4321-8765");

        when(userMapper.convertToEntity(requestUserDTO)).thenReturn(updateUser);

        // update 서비스 로직안에 변경 유저 전 데이터를 가져올때 필요
        when(userRepository.findById(convertedBeforeResponseUser.getId())).thenReturn(Optional.of(convertedBeforeResponseUser));
        when(userRepository.save(convertedBeforeResponseUser)).thenReturn(updateUser);
        when(userMapper.convertToDTO(updateUser)).thenReturn(requestUserDTO);

        //when
        UserDTO result = userService.updateUser(convertedBeforeResponseUser.getId(), requestUserDTO);

        //then
        assertNotNull(result);
        assertEquals(requestUserDTO.getName(), result.getName());
        assertEquals(requestUserDTO.getPhoneNumber(), result.getPhoneNumber());
    }

    @Test
    @DisplayName("유저 삭제 성공 테스트")
    void withDrawUser() {
        //given
        when(userRepository.findById(convertedBeforeResponseUser.getId())).thenReturn(Optional.of(convertedBeforeResponseUser));
        when(userRepository.save(convertedBeforeResponseUser)).thenReturn(convertedBeforeResponseUser);

        //when
        userService.withDrawUser(convertedBeforeResponseUser.getId());

        //then
        assertNotNull(convertedBeforeResponseUser.getUpdateTime());
        Assertions.assertEquals(UserType.STOP_ACTIVITY, convertedBeforeResponseUser.getUserType());
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logoutUserSuccess() {
        //given
        when(userMapper.convertToEntity(requestUserDTO)).thenReturn(convertedBeforeResponseUser);
        when(userRepository.findByUserIdAndPassword(convertedBeforeResponseUser.getUserId(), convertedBeforeResponseUser.getPassword()))
                .thenReturn(Optional.of(convertedBeforeResponseUser));
        when(userRepository.save(convertedBeforeResponseUser)).thenReturn(convertedBeforeResponseUser);
        when(userMapper.convertToDTO(convertedBeforeResponseUser)).thenReturn(requestUserDTO);

        //when
        UserDTO result = userService.loginUser(requestUserDTO, session);

        //then
        assertDoesNotThrow(() -> userService.logoutUser(session));
    }

    @Test
    @DisplayName("로그아웃 실패 테스트 - 로그인이 안되어 있을때")
    void logoutUserFail() {
        //when
        doThrow(new RuntimeException("Session is null")).when(session).invalidate();

        //then
        assertThrows(LogoutFailedException.class, () -> userService.logoutUser(session));
    }
}