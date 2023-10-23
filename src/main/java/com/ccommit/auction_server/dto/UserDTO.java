package com.ccommit.auction_server.dto;

import com.ccommit.auction_server.enums.UserType;
import com.ccommit.auction_server.validation.annotation.IsAdminValidation;
import com.ccommit.auction_server.validation.annotation.isExistEmailValidation;
import com.ccommit.auction_server.validation.annotation.isExistUserIdlValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "유저 DTO")
public class UserDTO {
    @NotBlank(groups = {SignUp.class, AdminSignUp.class, Login.class})
    @isExistUserIdlValidation(groups = {SignUp.class, AdminSignUp.class})
    @Schema(name = "userId", description = "유저 아이디", example = "testID")
    private String userId;

    @NotBlank(groups = {SignUp.class, AdminSignUp.class, UpdateUser.class, Login.class})
    @Pattern(groups = {SignUp.class, AdminSignUp.class, UpdateUser.class, Login.class},
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{3,20}$",
            message = "비밀번호는 3자기 이상 20자리 이하로 숫자와 영어, 특수기호(!@#$%^&*)를 각 하나이상 포함해주세요")
    @Schema(name = "password", description = "유저 비밀번호", example = "testPassword1!")
    private String password;

    @NotBlank(groups = {SignUp.class, AdminSignUp.class, UpdateUser.class})
    @Schema(name = "name", description = "유저 이름", example = "testName")
    private String name;

    @NotBlank(groups = {SignUp.class, UpdateUser.class})
    @Size(groups = {SignUp.class, UpdateUser.class},
            max = 20)
    @Pattern(groups = {SignUp.class, UpdateUser.class},
            regexp = "[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}", message = "올바른 전화번호 형식이 아닙니다.")
    @Schema(name = "phoneNumber", description = "유저 휴대폰 번호", example = "010-1234-5678")
    private String phoneNumber;

    @Email(groups = {SignUp.class, UpdateUser.class}, message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(groups = {SignUp.class, UpdateUser.class})
    @isExistEmailValidation(groups = {SignUp.class, UpdateUser.class})
    @Schema(name = "email", description = "유저 Email", example = "test@example.co.kr")
    private String email;

    @NotNull(groups = {SignUp.class, AdminSignUp.class})
    @IsAdminValidation(groups = {SignUp.class})
    @Schema(name = "userType", description = "유저 상태", example = "UNAUTHORIZED_USER")
    private UserType userType;

    @Schema(name = "createTime", description = "유저 생성 시간")
    private LocalDateTime createTime;

    public interface Login {
    }

    public interface SignUp {
    }

    public interface AdminSignUp {
    }

    public interface UpdateUser {
    }
}