package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.model.User;
import com.ccommit.auction_server.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/***
 * @Repository
 * - Spring 프레임워크에서 데이터 액세스 레이어를 담당하는 클래스에 부여되는 어노테이션
 * - 데이터베이스와의 상호작용을 위한 CRUD(Create, Read, Update, Delete) 기능을 수행하는 클래스를 간단하게 생성
 * - 데이터베이스와의 통신을 처리하는 데 필요한 기능들을 제공하며, 자동으로 인스턴스화되어 Spring의 컨테이너에 등록
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    Optional<User> findByIdAndUpdateTimeIsNull(Long id);

    Optional<User> findByUserId(String userId);

    Optional<User> findByUserIdAndPassword(String userId, String password);

    boolean existsByEmail(String email);

    boolean existsByUserId(String userId);

    UserProjection findUserProjectionById(Long id);
}