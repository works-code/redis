package com.code.redis.aspect;


import com.code.redis.annotation.RedisCacheable;
import com.code.redis.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class RedisCacheAspect {
    @Resource
    Environment environment;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(com.code.redis.annotation.RedisCacheable)")
    public Object doSomethingAround(ProceedingJoinPoint joinPoint) {
        log.error("### [RedisCacheAspect] Around : {}", joinPoint);
        Object object = null;

        // 어노테이션 값 얻어 오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method theMethod = ClassUtils.getMethodIfAvailable(joinPoint.getTarget().getClass(), signature.getMethod().getName(), signature.getParameterTypes());
        RedisCacheable redisCacheable = (RedisCacheable) theMethod.getAnnotation(RedisCacheable.class);

        // 키명 생성
        // 예) redis:new-song:local:C.0
        Object[] args = joinPoint.getArgs(); // 메소드 매개 변수
        Method method = signature.getMethod(); // 메소드 매개 변수 정보
        Map<String, Object> parameters = getAnnotatedParameterValue(method, args);
        String key = keyGenerator(redisCacheable, parameters);

        Object cachedObject = null;

        try{
            // 해당 키에 대한 값이 있는지 확인
            cachedObject = redisTemplate.opsForValue().get(key);
        } catch (Exception e){
            log.error("###  [RedisCacheAspect] Error : {}", e.getMessage());
        }
        // 있다면 해당 캐시 결과 제공 || 없다면 해당 메소드 실행하여 결과 리턴
        if(cachedObject == null) {
            // PROCEED !!!
            // 실제 메소드 실행 부분
            try{
                log.error("### [RedisCacheAspect] Around joinPoint start !!! ");
                object = joinPoint.proceed();
                log.error("### [RedisCacheAspect] Around joinPoint object : {}", object);
            }catch(Throwable e){
                log.error("###  [RedisCacheAspect] Error : {}", e.getMessage());
            }

            if(object != null) {
                redisTemplate.opsForValue().set(key, object, redisCacheable.ttl(), redisCacheable.unit());
                try{

                } catch (Exception e){
                    log.error("###  [RedisCacheAspect] Error : {}", e.getMessage());
                }
            }
        } else {
            object = cachedObject;
        }
        return object;
    }

    // 메소드 실행전 처리될 부분
    @Before("@annotation(com.code.redis.annotation.RedisCacheable)")
    public void doSomethingBefore() {
        log.error("### [RedisCacheAspect] Before");
    }

    // 메소드 실행후 처리될 부분
    @After("@annotation(com.code.redis.annotation.RedisCacheable)")
    public void doSomethingAfter() {
        log.error("### [RedisCacheAspect] After");
    }

    // map 형태로 값 담기
    private Map<String, Object> getAnnotatedParameterValue(Method method, Object[] args) {
        Map<String, Object> annotatedParameters = new HashMap<>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Parameter[] parameters = method.getParameters();

        ObjectMapper om = new ObjectMapper();

        int i = 0;
        for (Annotation[] annotations : parameterAnnotations) {
            Object arg = args[i];
            String name = parameters[i++].getDeclaringExecutable().getName(); // 실행 메소드 명
            try {
                // 어노테이션 값이 json 형태로 string에 저장
                String string = om.writeValueAsString(arg);
                // json 형태 값을 map 형태로 저장
                annotatedParameters.putAll(om.readValue(string, Map.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // key : 메소드명, value : 어노테이션 요청 객체
            annotatedParameters.put(name, arg);
        }
        return annotatedParameters;
    }

    // key 생성
    private String keyGenerator(RedisCacheable redisCacheable, Map<String, Object> parameters) {
        StringBuilder sb = new StringBuilder();
        String activeProfile = Arrays.toString(environment.getActiveProfiles()).replaceAll("\\[", "").replaceAll("\\]", "");

        sb.append(Arrays.toString(redisCacheable.cacheNames()).replaceAll("\\[", "").replaceAll("\\]", ""));
        sb.append(":");

        if(activeProfile.contains("local") || activeProfile.contains("development")) {
            sb.append(activeProfile);
            sb.append(":");
        }
        // 어노테이션에 있는 키에 값을 꺼내와서 해당 키값을 실제 저장할 캐시 키로 저장
        for(String key : redisCacheable.keys()) {
            sb.append(parameters.get(key));
            sb.append(".");
        }

        return sb.toString().substring(0, sb.toString().length() - 1);
    }

}
