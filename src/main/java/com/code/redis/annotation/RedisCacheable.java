package com.code.redis.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RedisCacheable {

    @AliasFor("cacheNames")
    String[] value() default {}; // 기본 값 셋팅시 cacheNames 변수에 데이터 매핑

    @AliasFor("value")
    String[] cacheNames() default {}; // 캐시 명

    String[] keys() default {}; // 키 값으로 쓸 파라미터

    long ttl() default 0L; // 캐시 유효 시간의 양

    TimeUnit unit() default TimeUnit.MILLISECONDS; // 캐시 시간 단위 (시, 분, 초)

}
