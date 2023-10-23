package com.ccommit.auction_server.model;

import com.ccommit.auction_server.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/***
 * @Entity 어노테이션은 JPA 엔티티 클래스를 정의할 때 사용하는 어노테이션입니다.
 * 이 클래스가 데이터베이스의 테이블과 매핑되는 엔티티 클래스임을 표시합니다.
 */
@Entity(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"user\"")
public class User {
    @Id
    /***
     * JPA가 자동으로 생성하는 기본키의 전략 중 하나 입니다.
     *
     * `GenerationType.AUTO'
     *
     * 데이터베이스에 따라 자동으로 기본 키 생성 전략을 선택
     * 데이터베이스에 의존적인 전략으로, 각 데이터베이스에 맞는 최적의 전략을 선택
     * `GenerationType.SEQUENCE'
     *
     * 데이터베이스의 시퀀스를 사용하여 기본 키를 생성
     * 주로 Oracle과 같은 데이터베이스에서 사용
     * `GenerationType.TABLE'
     *
     * 데이터베이스에 키 값을 저장하는 별도의 테이블을 사용하여 기본 키를 생성
     * 데이터베이스 종류에 관계없이 사용할 수 있지만, 성능상의 이슈가 발생할 수 있음
     * `GenerationType.IDENTITY'
     *
     * 데이터베이스의 자동 증가 컬럼을 사용하여 기본 키를 생성
     * 주로 MySQL, PostgreSQL과 같은 데이터베이스에서 사용
     * 데이터베이스에 기본 키 생성을 위임하여 처리하므로 일반적으로 가장 간단하고 효율적인 방법
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "user_type")
    private UserType userType;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateTime;

    @Column(name = "last_login_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastLoginTime;
}