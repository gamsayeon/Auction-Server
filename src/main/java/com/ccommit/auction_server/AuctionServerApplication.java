package com.ccommit.auction_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/***
 * @SpringBootAplication (@ Configuration + @ EnableAutoConfiguration + @ ComponentScan)
 * 스프링 부트 애플리케이션의 구성 요소를 간편하게 설정하고, 자동 구성과 컴포넌트 스캔과 같은 중요한 기능을 활성화하는 역할을 수행
 *
 *  @Configuration
 * 	- 해당 클래스가 스프링의 구성 요소를 정의하는 구성 클래스
 * 	- 어노테이션이 지정된 클래스는 스프링 빈(bean)의 정의나 설정을 담당
 *
 *  @EnableAutoConfiguration
 * 	- 스프링 부트는 클래스 경로와 설정된 의존성을 기반으로 애플리케이션을 자동으로 구성
 * 	- 스프링 부트의 자동 구성 기능을 사용하여 애플리케이션에 필요한 빈(bean)을 생성하고 구성
 *
 *  @ComponentScan
 * 	-  스프링이 애플리케이션 컨텍스트에서 구성 요소를 검색하고 등록
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableCaching
@EnableScheduling
public class AuctionServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionServerApplication.class, args);
    }

}
