package com.code.redis.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// 해당 설정을 해도 되고, application.yml에 설정 해도 됨 (application.yml에 설정해서 해당 부분은 주석)
@Data
@ConfigurationProperties(prefix = "spring.redis")
@Component
public class RedisProperties {
    private String host;
    private int port;
}
