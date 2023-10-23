package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.dto.UserDTO;
import com.ccommit.auction_server.mapper.UserMapper;
import com.ccommit.auction_server.model.User;
import com.ccommit.auction_server.enums.UserType;
import com.ccommit.auction_server.exception.AddFailedException;
import com.ccommit.auction_server.exception.LogoutFailedException;
import com.ccommit.auction_server.exception.NotMatchingException;
import com.ccommit.auction_server.exception.UpdateFailedException;
import com.ccommit.auction_server.repository.UserRepository;
import com.ccommit.auction_server.service.UserService;
import com.ccommit.auction_server.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    /***
     * @Transactional
     * - 어노테이션을 사용하면 데이터베이스 작업들이 하나의 트랜잭션으로 묶이게 됩니다.
     * - 트랜잭션 내에서 예외가 발생하면 해당 트랜잭션이 롤백이 됩니다.
     * - 예외가 발생한 경우에도 데이터 일관성을 보장
     *
     * 메서드에 Transactional 어노테이션이 적용
     * 예외가 발생시 DB CRUD 내용이 적용되지 않음(ROLLBACK)
     *
     * ex) 회원가입시 ID 유효성 검사를 통해 중복되어 있다면 Create 하지 않게 할 수 있다.
     * 또는 회원가입시 DB Connection exception 이 발생한다면 ROLLBACK 으로 인해 추가 되지 않는다.
     * @param userDTO
     * @return
     */
    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        User user = userMapper.convertToEntity(userDTO);

        if (userDTO.getUserType() != UserType.ADMIN) {
            user.setUserType(UserType.UNAUTHORIZED_USER);
        }

        user.setCreateTime(LocalDateTime.now());
        User resultUser = userRepository.save(user);
        if (resultUser != null) {
            UserDTO resultUserDTO = userMapper.convertToDTO(resultUser);
            if (resultUserDTO == null) {
                logger.warn("매핑에 실패했습니다.");
                throw new NotMatchingException("COMMON_NOT_MATCHING_MAPPER", userDTO);
            } else {
                logger.info("유저 " + resultUser.getUserId() + "을 회원가입에 성공했습니다.");
                return resultUserDTO;
            }
        } else {
            logger.warn("회원가입 오류. 재시도 해주세요.");
            throw new AddFailedException("USER_ADD_FAILED", userDTO);
        }

    }

    @Override
    @Transactional
    public UserDTO updateUserType(String userId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty() || optionalUser.get().getUserType() == UserType.STOP_ACTIVITY) {
            logger.warn("해당 유저를 찾을수 없습니다.");
            throw new NotMatchingException("USER_NOT_MATCH", userId);
        } else {
            optionalUser.get().setUserType(UserType.USER);
            User resultUser = userRepository.save(optionalUser.get());
            UserDTO resultUserDTO = userMapper.convertToDTO(resultUser);
            if (resultUser != null) {
                logger.info("인증되었습니다.");
                return resultUserDTO;
            } else {
                logger.warn("타입을 수정하지 못했습니다. 다시 인증해주세요.");
                throw new UpdateFailedException("USER_UPDATE_FAILED_TYPE", "retry");
            }
        }
    }

    @Override
    @Transactional
    public UserDTO loginUser(UserDTO userDTO, HttpSession session) {
        User user = userMapper.convertToEntity(userDTO);
        Optional<User> optionalUser = userRepository.findByUserIdAndPassword(user.getUserId(), user.getPassword());

        if (optionalUser.isEmpty()) {
            logger.warn("로그인에 실패 했습니다. 아이디 및 비밀번호 확인 후 재시도 해주세요.");
            throw new NotMatchingException("USER_NOT_MATCH_LOGIN", userDTO);
        } else if (optionalUser.get().getUserType() == UserType.STOP_ACTIVITY) {
            logger.warn("이상 유저 입니다.");
            throw new NotMatchingException("USER_NOT_MATCH_TYPE", UserType.STOP_ACTIVITY);
        } else {
            User resultUser = optionalUser.get();
            this.insertSession(session, resultUser.getId(), resultUser.getUserType());
            resultUser.setLastLoginTime(LocalDateTime.now());
            resultUser = userRepository.save(resultUser);
            if (resultUser == null) {
                logger.warn("마지막 로그인 시간을 업데이트 하는데 실패하였습니다.");
                throw new UpdateFailedException("USER_UPDATE_FAILED_UPDATE_TIME", "retry login");
            } else {
                UserDTO resultUserDTO = userMapper.convertToDTO(optionalUser.get());
                if (resultUserDTO == null) {
                    logger.warn("매핑에 실패했습니다.");
                    throw new NotMatchingException("COMMON_NOT_MATCHING_MAPPER", userDTO);
                } else {
                    logger.info("로그인에 성공하였습니다.");
                    return resultUserDTO;
                }
            }
        }

    }

    @Override
    @Transactional
    public void insertSession(HttpSession session, Long id, UserType userType) {
        SessionUtil.setLoginSession(session, id, userType);
    }

    @Override
    public UserDTO selectUser(Long id) {
        Optional<User> optionalUser = userRepository.findByIdAndUpdateTimeIsNull(id);

        if (optionalUser.isEmpty()) {
            logger.warn("해당 유저를 찾지 못했습니다. 재시도 해주세요.");
            throw new NotMatchingException("USER_NOT_MATCH", id);
        } else if (optionalUser.get().getUserType() == UserType.STOP_ACTIVITY) {
            logger.warn("이상 유저입니다.");
            throw new NotMatchingException("USER_NOT_MATCH_TYPE", UserType.STOP_ACTIVITY);
        }

        UserDTO resultUserDTO = userMapper.convertToDTO(optionalUser.get());
        if (resultUserDTO == null) {
            logger.warn("매핑에 실패했습니다.");
            throw new NotMatchingException("COMMON_NOT_MATCHING_MAPPER", id);
        } else {
            logger.info("회원 조회에 성공하였습니다.");
            return resultUserDTO;
        }
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userMapper.convertToEntity(userDTO);

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.warn("유저를 찾지 못했습니다.");
            throw new NotMatchingException("USER_NOT_MATCH", id);
        } else {
            User resultUser = optionalUser.get();
            resultUser.setPassword(user.getPassword());
            resultUser.setName(user.getName());
            resultUser.setPhoneNumber(user.getPhoneNumber());
            resultUser.setEmail(user.getEmail());
            resultUser = userRepository.save(resultUser);
            if (resultUser != null) {
                UserDTO resultUserDTO = userMapper.convertToDTO(resultUser);
                if (resultUserDTO != null) {
                    logger.info("회원정보를 성공적으로 수정했습니다.");
                    return resultUserDTO;
                } else {
                    logger.warn("매핑에 실패했습니다.");
                    throw new NotMatchingException("COMMON_NOT_MATCHING_MAPPER", userDTO);
                }
            } else {
                logger.warn("회원정보를 수정하지 못했습니다. 재시도 해주세요");
                throw new UpdateFailedException("USER_UPDATE_FAILED", "retry");
            }
        }
    }

    @Override
    @Transactional
    public void withDrawUser(Long id) {
        Optional<User> optionalResultUser = userRepository.findById(id);
        if (optionalResultUser.isEmpty()) {
            logger.warn("해당하는 유저가 없습니다. 다시 확인해주세요.");
            throw new NotMatchingException("USER_NOT_MATCH", id);
        } else if (optionalResultUser.get().getUserType() == UserType.STOP_ACTIVITY) {
            logger.warn("이상이 있는 유저입니다.(회원 삭제, 이상유저 제재 등의 이유로 삭제 불가)");
            throw new UpdateFailedException("PRODUCT_UPDATE_FAILED_BY_USER_TYPE", id);
        } else {
            User resultUser = optionalResultUser.get();
            resultUser.setUpdateTime(LocalDateTime.now());
            resultUser.setUserType(UserType.STOP_ACTIVITY);
            resultUser = userRepository.save(resultUser);
            if (resultUser == null) {
                logger.warn("회원 삭제 업데이트에 실패했습니다.");
                throw new UpdateFailedException("USER_UPDATE_FAILED_DELETE", id);
            } else {
                logger.info("회원 삭제 업데이트에 성공했습니다.");
            }
        }
    }

    @Override
    @Transactional
    public void logoutUser(HttpSession session) {
        try {
            SessionUtil.clear(session);
            logger.info("로그아웃에 성공했습니다.");
        } catch (Exception e) {
            logger.warn("로그아웃에 실패했습니다.");
            throw new LogoutFailedException("USER_LOGOUT_FAILED");
        }
    }

}