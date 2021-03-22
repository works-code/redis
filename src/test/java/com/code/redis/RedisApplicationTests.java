package com.code.redis;

import com.code.redis.service.JobService;
import com.code.redis.service.RedisService;
import com.code.redis.vo.Job;
import com.code.redis.vo.JobTypeEnum;
import com.code.redis.vo.Struct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
class RedisApplicationTests {

    @Autowired
    private JobService jobService;

    @Autowired
    private RedisService redisService;

    @Test
    void string_redis() {
        String key = "key";
        String value = "value";
        redisService.setStringOps(key, value, 10, TimeUnit.SECONDS);
        log.error("### Redis Key => {} | value => {}", key, redisService.getStringOps(key));
    }

    @Test
    void list_redis() {
        String key = "key_list";
        String keyEty = "key_empty";
        List<String> values = new ArrayList<>();
        values.add("value_1");
        values.add("value_2");
        redisService.setListOps(key, values);
        log.error("### Redis Key => {} | value => {}", key, redisService.getListOps(key));
        log.error("### Redis Empty Key => {} | value => {}", keyEty, redisService.getListOps(keyEty));

    }

    @Test
    void hash_redis() {
        String key = "key_hash";
        String keyEty = "key_empty";

        String mapKeyOne = "map_key_1";
        String mapKeyTwo = "map_key_2";

        HashMap<String, String> map = new HashMap<>();
        map.put(mapKeyOne, "value_1");
        map.put(mapKeyTwo, "value_2");

        redisService.setHashOps(key, map);
        log.error("### Redis One Key => {} | value => {}", key, redisService.getHashOps(key, mapKeyOne));
        log.error("### Redis Two Key => {} | value => {}", key, redisService.getHashOps(key, mapKeyTwo));
        log.error("### Redis Empty hash Key => {} | value => {}", keyEty, redisService.getHashOps(key, keyEty));
        log.error("### Redis Empty Key => {} | value => {}", keyEty, redisService.getHashOps(keyEty, mapKeyOne));
    }

    @Test
    void set_redis() {
        String key = "key_set";
        String keyEty = "key_empty";
        redisService.setSetOps(key, "value_1","value_2","value_1");
        log.error("### Redis Key => {} | value => {}", key, redisService.getSetOps(key));
        log.error("### Redis Empty Key => {} | value => {}", keyEty, redisService.getSetOps(keyEty));
    }

    @Test
    void sortedSet_redis() {
        String key = "key_sortedSet";
        String keyEty = "key_empty";
        List<Struct.SortedSet> values = new ArrayList<>();
        values.add(new Struct.SortedSet(){{
            setValue("value_100");
            setScore(100D);
        }});
        values.add(new Struct.SortedSet(){{
            setValue("value_10");
            setScore(10D);
        }});
        redisService.setSortedSetOps(key, values);
        log.error("### Redis Key => {} | value => {}", key, redisService.getSortedSetOps(key));
        log.error("### Redis Empty Key => {} | value => {}", keyEty, redisService.getSortedSetOps(keyEty));
    }

    // json으로 저장 할거라 redistemplate 사용
    @Test
    void 레디스캐시테스트(){
      /*  log.error("### => {}", jobService.chart(new Job.request(){{
            setType(JobTypeEnum.B);
        }}));
        log.error("### => {}", jobService.newSong(new Job.request(){{
            setType(JobTypeEnum.A);
            setNumber(1);
        }}));*/
        log.error("### => {}", jobService.newSong(new Job.request(){{
            setType(JobTypeEnum.A);
            setNumber(2);
        }}));
       /* log.error("### => {}", jobService.newSong(new Job.request(){{
            setType(JobTypeEnum.B);
            setNumber(1);
        }}));*/
    }
}
