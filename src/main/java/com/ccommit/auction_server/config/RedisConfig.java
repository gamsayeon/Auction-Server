package com.ccommit.auction_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${expire.defaultTime}")
    private long defaultExpireSecond;

    /**
     * Jedis 와 Lettuce 의 차이
     * Jedis
     * - JAVA 의 표준 Redis 클라이언트, 레거시 I/O코드를 사용
     * - 비동기 처리시 별도의 라이브러리 사용
     * Lettuce
     * - Netty(비동기 이벤트 기반 고성능 네트워크 프레임워크) 기반의 Redis 클라이언트
     * - 비동기로 요청을 처리 하기 때문에 고성능을 자랑
     * <p>
     * Lettuce 는 TPS/CPU/Connection 개수/응답속도 등 전 분야에서 우위
     * 참고 : https://jojoldu.tistory.com/418
     * <p>
     * Jedis 에 비해 몇배 이상의 성능과 하드웨터 자원 절약이 가능함
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(defaultExpireSecond))
                .disableCachingNullValues()
                .computePrefixWith(CacheKeyPrefix.simple())
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer(StandardCharsets.UTF_8)));

        return RedisCacheManager.builder(RedisCacheWriter.lockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(cacheConfiguration)
                .transactionAware()
                .build();
    }
}
