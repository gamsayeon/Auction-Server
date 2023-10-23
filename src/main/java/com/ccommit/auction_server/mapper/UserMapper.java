package com.ccommit.auction_server.mapper;

import com.ccommit.auction_server.dto.UserDTO;
import com.ccommit.auction_server.model.User;
import com.ccommit.auction_server.util.Sha256Encrypt;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    /***
     * ModelMapper는 자바 객체 간의 매핑을 간편하게 수행하기 위한 라이브러리
     * - 주로 DTO 와 Entity간의 데이터 전달과 변환을 위해 사용
     * - 복잡한 매핑 규칙을 간단하게 설정하여 개발 생산성을 높일수 있습니다.
     */
    private final ModelMapper modelMapper;
  
    /***
     * Entity <-> DTO 를 하는 이유
     *
     * - Entity는 데이터베이스의 테이블과 매핑되는 자바 객체
     * - DTO는 Data Transfer Object의 약자로, 클라이언트와 서버 간의 데이터 전송을 위한 객체
     *
     * - Entity는 데이터베이스의 구조를 반영하므로, 클라이언트에게 민감한 정보가 직접 노출되기에 보안상의 이유로 DTO로 변환하여 필요한 데이터만을 전달합니다.
     * - DTO는 클라이언트가 필요한 최소한의 정보만을 포함하여 Entity보다 네트워크의 부하가 감소합니다.
     * - 외부 API의 버전의 관리 용이성이 증가합니다. 새로운 외부 API의 버전이 증가하게 되면 기존의 DTO을 수정할지 아니면 새로운 DTO를 추가할지 결정합니다.
     *      이로써 이전버전과의 하위호환성이 유지할수 있어 버전의 관리가 용이합니다.
     * @param userDTO
     * @return user
     */
    public User convertToEntity(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(Sha256Encrypt.encrypt(userDTO.getPassword()));
        return user;
    }

    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
    }
}
