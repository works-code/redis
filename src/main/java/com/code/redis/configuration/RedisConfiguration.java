package com.code.redis.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableRedisRepositories
@Configuration
public class RedisConfiguration {

    @Autowired
    RedisProperties redisProperties;

    // lettuce 사용시 해당 빈 정의 필요
    /*@Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort()));
    }*/

    // jedis 사용시 해당 빈 정의 필요
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        // jedis pool 사용시 주석을 풀어 사용하세요
        /*RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(config);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisConnectionFactory.setUsePool(true);
        jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setMaxIdle(10);
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
        return jedisConnectionFactory;*/
        return new JedisConnectionFactory(new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort()));
    }

    // json 형태로 값이 저장될 필요가 없다면 아래 빈 정의 필요 없음
    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // redis client 에서 데이터 확인시 알 수 없는 형태로 출력 되는것을 방지
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class)); // GenericJackson2JsonRedisSerializer 는 Json 형태로 확인 가능하도록 하기위함
        // Hash Operation(opsForHash) 사용시
        /*redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());*/
        return redisTemplate;
    }


}
