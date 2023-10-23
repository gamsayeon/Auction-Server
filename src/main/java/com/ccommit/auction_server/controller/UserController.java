package com.ccommit.auction_server.controller;

import com.ccommit.auction_server.dto.UserDTO;
import com.ccommit.auction_server.aop.LoginCheck;
import com.ccommit.auction_server.model.CommonResponse;
import com.ccommit.auction_server.service.serviceImpl.EmailServiceImpl;
import com.ccommit.auction_server.service.serviceImpl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/users")
@RestController
@Log4j2
/***
 * 생성자 주입을 자동으로 처리 함
 */
@RequiredArgsConstructor
@Tag(name = "User API", description = "User 관련 API")
public class UserController {

    private final UserServiceImpl userService;
    private final EmailServiceImpl emailService;

    private final Logger logger = LogManager.getLogger(UserController.class);

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "USER_ADD_FAILED : 회원가입 오류<br>" +
                    "COMMON_NOT_MATCHING_MAPPER : 매핑 오류<br>" +
                    "USER_DUPLICATE_ID : user Id 중복<br>" +
                    "USER_DUPLICATE_EMAIL : user email 중복<br>" +
                    "USER_USER_ACCESS_DENIED : user type이 ADMIN으로 가입시 실패", content = @Content),
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    @Operation(summary = "User 회원가입",
            description = "유저의 정보를 추가합니다. 하단의 UserDTO 참고",
            method = "POST",
            tags = "User API",
            operationId = "Register User")
    public ResponseEntity<CommonResponse<UserDTO>> registerUser(@RequestBody @Validated({UserDTO.SignUp.class}) UserDTO userDTO,
                                                                HttpServletRequest request) {
        logger.debug("회원을 가입합니다.");
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "회원가입에 성공했습니다.",
                request.getRequestURI(), userService.registerUser(userDTO));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "USER_NOT_MATCH : 해당 유저가 없음<br>" +
                    "USER_UPDATE_FAILED_TYPE : 타입 업데이트 오류<br>" +
                    "EMAIL_CACHE_TTL_OUT : Token TTL 시간 만료", content = @Content),
            @ApiResponse(responseCode = "200", description = "회원 타입 변경 성공", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    @Operation(summary = "User Type 업데이트",
            description = "유저 인증을 거친 후 유저 상태를 변경합니다.",
            method = "GET",
            tags = "User API",
            operationId = "Update User Type")
    public ResponseEntity<CommonResponse<UserDTO>> verifyAccount(@RequestParam("token") String token,
                                                                 HttpServletRequest request) {
        // 토큰을 이용한 회원 가입 인증 처리
        String id = emailService.verifyEmail(token);
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "이메일 인증에 성공했습니다.",
                request.getRequestURI(), userService.updateUserType(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "USER_ADD_FAILED : 회원가입 오류<br>" +
                    "COMMON_NOT_MATCHING_MAPPER : 매핑 오류<br>" +
                    "USER_DUPLICATE_ID : user Id 중복", content = @Content),
            @ApiResponse(responseCode = "200", description = "관리자 회원가입 성공", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    @Operation(summary = "User Admin 회원가입",
            description = "관리자 유저의 정보를 추가합니다. 하단의 UserDTO 참고",
            method = "POST",
            tags = "User API",
            operationId = "Register Admin User")
    public ResponseEntity<CommonResponse<UserDTO>> registerAdminUser(@RequestBody @Validated({UserDTO.AdminSignUp.class}) UserDTO userDTO,
                                                                     HttpServletRequest request) {
        logger.debug("ADMIN 회원을 가입합니다.");
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "ADMIN 유저 회원가입에 성공했습니다.",
                request.getRequestURI(), userService.registerUser(userDTO));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "USER_NOT_MATCH_LOGIN : ID or Password 불일치<br>" +
                    "USER_NOT_MATCH_TYPE : 이상 유저 타입으로 인한 로그인 불가<br>" +
                    "USER_UPDATE_FAILED_UPDATE_TIME : 유저 마지막 로그인 시간 업데이트 실패<br>" +
                    "COMMON_NOT_MATCHING_MAPPER : 매핑 오류", content = @Content),
            @ApiResponse(responseCode = "200", description = "회원 로그인 성공", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    @Operation(summary = "User 로그인",
            description = "User Id와 Password로 로그인합니다.",
            method = "POST",
            tags = "User API",
            operationId = "Login User")
    public ResponseEntity<CommonResponse<UserDTO>> loginUser(@RequestBody @Validated({UserDTO.Login.class}) UserDTO userDTO,
                                                             HttpSession session, HttpServletRequest request) {
        logger.debug("유저 로그인합니다.");
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "로그인에 성공했습니다.",
                request.getRequestURI(), userService.loginUser(userDTO, session));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-email")
    @LoginCheck(types = LoginCheck.LoginType.UNAUTHORIZED_USER)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "USER_NOT_MATCH : 해당 유저가 없음<br>" +
                    "USER_NOT_MATCH_TYPE : 이상 유저 타입으로 인한 이메일 전송 불가<br>" +
                    "EMAIL_SEND_FAILED : 이메일 전송 실패<br>" +
                    "COMMON_NOT_MATCHING_MAPPER : 매핑 오류", content = @Content),
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    @Operation(summary = "User Email 발송",
            description = "로그인한 유저 이메일로 인증 토큰을 발송합니다.",
            method = "POST",
            tags = "User API",
            operationId = "Send User Email")
    public ResponseEntity<CommonResponse<String>> sendEmail(@Parameter(hidden = true) Long loginId, HttpServletRequest request) {
        logger.debug("인증 이메일을 재전송 합니다.");
        UserDTO resultUserDTO = userService.selectUser(loginId);
        String token = UUID.randomUUID().toString();
        emailService.putCacheToken(token, resultUserDTO.getUserId());
        emailService.sendTokenToUser(token, resultUserDTO.getEmail());
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "이메일 전송에 성공했습니다.",
                request.getRequestURI(), resultUserDTO.getEmail() + " Email을 확인해주세요");
        return ResponseEntity.ok(response);
    }

    @LoginCheck(types = {LoginCheck.LoginType.USER, LoginCheck.LoginType.ADMIN,
            LoginCheck.LoginType.UNAUTHORIZED_USER})
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "USER_NOT_MATCH : 해당 유저가 없음<br>" +
                    "USER_NOT_MATCH_TYPE : 이상 유저 타입으로 인한 조회 불가<br>" +
                    "COMMON_NOT_MATCHING_MAPPER : 매핑 오류", content = @Content),
            @ApiResponse(responseCode = "200", description = "유저 조회 성공", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    @Operation(summary = "User 조회",
            description = "자신의 유저 정보를 조회 합니다.",
            method = "GET",
            tags = "User API",
            operationId = "Select User")
    public ResponseEntity<CommonResponse<UserDTO>> selectUser(@Parameter(hidden = true) Long loginId, HttpServletRequest request) {
        logger.debug("유저를 조회합니다.");
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "회원을 조회하는데 성공했습니다.",
                request.getRequestURI(), userService.selectUser(loginId));
        return ResponseEntity.ok(response);
    }

    @LoginCheck(types = {LoginCheck.LoginType.USER, LoginCheck.LoginType.ADMIN,
            LoginCheck.LoginType.UNAUTHORIZED_USER})
    @PatchMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "USER_NOT_MATCH : 해당 유저가 없음<br>" +
                    "USER_UPDATE_FAILED : 유저 정보 업데이트 실패<br>" +
                    "USER_DUPLICATE_EMAIL : user email 중복<br>" +
                    "COMMON_NOT_MATCHING_MAPPER : 매핑 오류", content = @Content),
            @ApiResponse(responseCode = "200", description = "회원 정보 업데이트 성공", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    @Operation(summary = "User 정보 수정",
            description = "로그인한 유저의 정보를 수정합니다.",
            method = "PATCH",
            tags = "User API",
            operationId = "Update User")
    public ResponseEntity<CommonResponse<UserDTO>> updateUser(@Parameter(hidden = true) Long loginId, @RequestBody @Validated({UserDTO.UpdateUser.class}) UserDTO userDTO,
                                                              HttpServletRequest request) {
        logger.debug("유저를 수정합니다.");
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "회원 정보를 수정했습니다.",
                request.getRequestURI(), userService.updateUser(loginId, userDTO));
        return ResponseEntity.ok(response);
    }

    @LoginCheck(types = {LoginCheck.LoginType.ADMIN})
    @PatchMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "USER_NOT_MATCH : 해당 유저가 없음<br>" +
                    "USER_UPDATE_FAILED : 유저 정보 업데이트 실패<br>" +
                    "COMMON_NOT_MATCHING_MAPPER : 매핑 오류", content = @Content),
            @ApiResponse(responseCode = "200", description = "회원 정보 업데이트 성공", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    @Operation(summary = "Admin으로 인한 User 정보 수정",
            description = "관리자로 인한 해당하는 유저의 정보를 수정합니다.",
            method = "PATCH",
            tags = "User API",
            operationId = "Update User By Admin")
    @Parameter(name = "id", description = "수정할 유저 번호", example = "1")
    public ResponseEntity<CommonResponse<UserDTO>> updateUserByAdmin(@Parameter(hidden = true) Long loginId, @PathVariable("id") Long id,
                                                                     @RequestBody @Validated({UserDTO.UpdateUser.class}) UserDTO userDTO,
                                                                     HttpServletRequest request) {
        logger.debug("유저를 수정합니다.");
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "ADMIN이 해당하는 회원의 정보를 수정했습니다.",
                request.getRequestURI(), userService.updateUser(id, userDTO));
        return ResponseEntity.ok(response);
    }

    @LoginCheck(types = {LoginCheck.LoginType.USER, LoginCheck.LoginType.UNAUTHORIZED_USER, LoginCheck.LoginType.ADMIN})
    @PatchMapping("/withdraw")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "USER_NOT_MATCH : 해당 유저가 없음<br>" +
                    "PRODUCT_UPDATE_FAILED_BY_USER_TYPE : 이상 유저 타입으로 인한 삭제 오류<br>" +
                    "USER_UPDATE_FAILED_DELETE : 유저 삭제 오류", content = @Content),
            @ApiResponse(responseCode = "200", description = "유저 삭제 성공", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    @Operation(summary = "User 삭제",
            description = "로그인한 유저를 삭제합니다.",
            method = "PATCH",
            tags = "User API",
            operationId = "Delete User")
    @Parameter(name = "arg0", hidden = true)
    public ResponseEntity<CommonResponse<String>> withDrawUser(@Parameter(hidden = true) Long loginId, HttpSession session,
                                                               HttpServletRequest request) {
        logger.debug("유저를 탈퇴합니다.");
        userService.withDrawUser(loginId);
        userService.logoutUser(session);
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "해당 유저를 탈퇴했습니다.",
                request.getRequestURI(), loginId + " User delete Success");
        return ResponseEntity.ok(response);
    }

    @LoginCheck(types = {LoginCheck.LoginType.ADMIN})
    @PatchMapping("/withdraw/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "USER_NOT_MATCH : 해당 유저가 없음<br>" +
                    "PRODUCT_UPDATE_FAILED_BY_USER_TYPE : 이상 유저 타입으로 인한 삭제 오류<br>" +
                    "USER_UPDATE_FAILED_DELETE : 유저 삭제 오류", content = @Content),
            @ApiResponse(responseCode = "200", description = "유저 삭제 성공", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    @Operation(summary = "Admin으로 인한 User 삭제",
            description = "관리자로 인한 해당하는 유저를 삭제합니다.",
            method = "PATCH",
            tags = "User API",
            operationId = "Delete User By Admin")
    @Parameter(name = "id", description = "삭제할 유저 번호", example = "1")
    public ResponseEntity<CommonResponse<String>> withDrawByAdmin(@Parameter(hidden = true) Long loginId, @PathVariable("id") Long id,
                                                                  HttpServletRequest request) {
        logger.debug("ADMIN 계정으로 유저를 탈퇴합니다.");
        userService.withDrawUser(id);
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "ADMIN이 이상유저를 탈퇴했습니다.",
                request.getRequestURI(), id + "User delete Success");
        return ResponseEntity.ok(response);
    }

    @LoginCheck(types = {LoginCheck.LoginType.USER, LoginCheck.LoginType.ADMIN,
            LoginCheck.LoginType.UNAUTHORIZED_USER})
    @PostMapping("/logout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "USER_LOGOUT_FAILED : 로그아웃 실패", content = @Content),
            @ApiResponse(responseCode = "200", description = "회원 로그아웃 성공", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    @Operation(summary = "User Logout",
            description = "유저를 로그아웃 합니다.",
            method = "POST",
            tags = "User API",
            operationId = "Logout User")
    public ResponseEntity<CommonResponse<String>> logoutUser(@Parameter(hidden = true) Long loginId, HttpSession session,
                                                             HttpServletRequest request) {
        logger.debug("유저 로그아웃합니다.");
        userService.logoutUser(session);
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "회원 로그아웃 했습니다.",
                request.getRequestURI(), loginId + "logout");
        return ResponseEntity.ok(response);
    }
}
