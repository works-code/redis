package com.code.redis.service;

import com.code.redis.vo.Struct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/***
 * String에 특화된 StringRedisTemplate
 * String 만 다루려면 아래 서비스 사용하고, 그게 아니라면 RedisTemplate 사용.
 */
@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // string (opsForValue)
    public void setStringOps(String key, String value, long ttl, TimeUnit unit){
        redisTemplate.opsForValue().set(key, value, ttl, unit);
    }

    public String getStringOps(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    // list (opsForList)
    public void setListOps(String key, List<String> values){
        redisTemplate.opsForList().rightPushAll(key, values);
    }

    public List<String> getListOps(String key){
        Long len = redisTemplate.opsForList().size(key);
        return len == 0 ? new ArrayList<>() : redisTemplate.opsForList().range(key, 0, len-1);
    }

    // hash (opsForHash)
    public void setHashOps(String key, HashMap<String, String> value){
        redisTemplate.opsForHash().putAll(key, value);
    }

    public String getHashOps(String key, String hashKey){
        return redisTemplate.opsForHash().hasKey(key, hashKey) ? (String) redisTemplate.opsForHash().get(key, hashKey) : new String();
    }

    // set (opsForSet)
    public void setSetOps(String key, String... values){
        redisTemplate.opsForSet().add(key, values);
    }

    public Set<String> getSetOps(String key){
        return redisTemplate.opsForSet().members(key);
    }

    // sorted set (opsForZSet)
    public void setSortedSetOps(String key, List<Struct.SortedSet> values){
        for(Struct.SortedSet v : values){
            redisTemplate.opsForZSet().add(key, v.getValue(), v.getScore());
        }
    }

    public Set getSortedSetOps(String key){
        Long len = redisTemplate.opsForZSet().size(key);
        return len == 0 ? new HashSet<String>() : redisTemplate.opsForZSet().range(key, 0, len-1);
    }

}
