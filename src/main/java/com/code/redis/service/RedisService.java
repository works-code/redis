package com.code.redis.service;

import com.code.redis.vo.Struct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/***
 * String에 특화된 StringRedisTemplate
 * String 만 다루려면 아래 서비스 사용하고, 그게 아니라면 RedisTemplate 빈 정의하여 사용.
 */
@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    RedisConnectionFactory redisConnectionFactory;

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

    public List<String> getAsterOps(String key){
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        ScanOptions options = ScanOptions.scanOptions().count(2).match(key).build();

        List<String> values = new ArrayList<>();
        Cursor<byte[]> cursor = redisConnection.scan(options);

        while (cursor.hasNext()){
            String val = new String(cursor.next());
            values.add(val);
        }

        return values;
    }

    public Long delAsterOps(String key){
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        ScanOptions options = ScanOptions.scanOptions().count(2).match(key).build();

        List<String> values = new ArrayList<>();
        Cursor<byte[]> cursor = redisConnection.scan(options);

        while (cursor.hasNext()){
            String val = new String(cursor.next());
            values.add(val);
        }

        Long resultCode = redisTemplate.delete(values);

        return resultCode;
    }

    public List<String> getHashAsterOps(String key){
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        ScanOptions options = ScanOptions.scanOptions().count(10).match("*").build();
        Cursor<byte[]> outCursor = redisConnection.scan(options);

        List<String> values = new ArrayList<>();

        // out key
        while (outCursor.hasNext()){
            String outKey = new String(outCursor.next());

            ScanOptions hashOptions = ScanOptions.scanOptions().count(10).match(key).build();
            Cursor<Map.Entry<byte[],byte[]>> inCursor = redisConnection.hScan(outKey.getBytes(), hashOptions);

            // in key
            while (inCursor.hasNext()){
                Map.Entry<byte[], byte[]> val = inCursor.next();
                values.add(outKey+"|"+new String(val.getKey())+"|"+new String(val.getValue()));
            }
        }

        return values;
    }
    public Long delHashAsterOps(String key){
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        ScanOptions options = ScanOptions.scanOptions().count(10).match("*").build();
        Cursor<byte[]> outCursor = redisConnection.scan(options);

        Long resultCode = 0L;

        // out key
        while (outCursor.hasNext()){
            String outKey = new String(outCursor.next());

            ScanOptions hashOptions = ScanOptions.scanOptions().count(10).match(key).build();
            Cursor<Map.Entry<byte[],byte[]>> inCursor = redisConnection.hScan(outKey.getBytes(), hashOptions);

            // in key
            while (inCursor.hasNext()){
                Map.Entry<byte[], byte[]> val = inCursor.next();
                Long delCnt = redisTemplate.opsForHash().delete(outKey, new String(val.getKey()));
                resultCode += delCnt;
            }
        }

        return resultCode;
    }

}
