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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
        String key = "s_key03";
        String value = "s_value03";
        redisService.setStringOps(key, value, 1, TimeUnit.HOURS);
        log.error("### Redis Key => {} | value => {}", key, redisService.getStringOps(key));
    }

    @Test
    void list_redis() {
        String key = "list_redis";
        List<String> values = new ArrayList<>();
        values.add("list_01");
        values.add("list_02");
        redisService.setListOps(key, values);
        log.error("### Redis Key => {} | value => {}", key, redisService.getListOps(key));

        String keyEty = "empty_key";
        log.error("### Redis Empty Key => {} | value => {}", keyEty, redisService.getListOps(keyEty));
    }

    @Test
    void hash_redis() {
        String key = "h_key_20210730";

        HashMap<String, String> map = new HashMap<>();
        String mapKeyOne = "key_01";
        String mapKeyTwo = "key_02";

        map.put(mapKeyOne, "value_03");
        map.put(mapKeyTwo, "value_04");

        redisService.setHashOps(key, map);
        log.error("### Redis One Key => {} | value => {}", key, redisService.getHashOps(key, mapKeyOne));
        log.error("### Redis Two Key => {} | value => {}", key, redisService.getHashOps(key, mapKeyTwo));

        String keyEty = "key_empty";
        log.error("### Redis Empty hash Key => {} | value => {}", keyEty, redisService.getHashOps(key, keyEty));
        log.error("### Redis Empty Key => {} | value => {}", keyEty, redisService.getHashOps(keyEty, mapKeyOne));
    }

    @Test
    void set_redis() {
        String key = "key_set";

        redisService.setSetOps(key, "value_1","value_2","value_1");
        log.error("### Redis Key => {} | value => {}", key, redisService.getSetOps(key));

        String keyEty = "key_empty";
        log.error("### Redis Empty Key => {} | value => {}", keyEty, redisService.getSetOps(keyEty));
    }

    @Test
    void sortedSet_redis() {
        String key = "sortedSet_redis";

        List<Struct.SortedSet> values = new ArrayList<>();
        values.add(new Struct.SortedSet(){{
            setValue("sortedSet_value_100");
            setScore(100D);
        }});
        values.add(new Struct.SortedSet(){{
            setValue("sortedSet_value_10");
            setScore(10D);
        }});
        redisService.setSortedSetOps(key, values);
        log.error("### Redis Key => {} | value => {}", key, redisService.getSortedSetOps(key));

        String keyEty = "key_empty";
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

    @Test
    void 조회테스트(){
        //List<String> value = redisService.getAsterOps("*07*");
        List<String> value = redisService.getHashAsterOps("*02");

        log.error("## => {}", value);
    }

    @Test
    void 삭제테스트(){
        Long rtnCode = redisService.delHashAsterOps("*01");

        log.error("## => {}", rtnCode);
    }


}
