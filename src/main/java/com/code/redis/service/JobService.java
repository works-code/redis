package com.code.redis.service;

import com.code.redis.annotation.RedisCacheable;
import com.code.redis.vo.Job;
import com.code.redis.vo.RedisCaches;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
public class JobService {

    // CHART : 10분 동안 json 데이터 캐싱
    @RedisCacheable(cacheNames = {RedisCaches.REDIS_CHART_CACHE}, keys = {"type"}, ttl = 10, unit = TimeUnit.MINUTES)
    public Object chart(Job.request request){
        Job.response response = new Job.response(){{
            setJobName("A");
            setType(request.getType());
        }};
        return response;
    }

    // NEW-SONG : 10초 동안 json 데이터 캐싱
    @RedisCacheable(cacheNames = {RedisCaches.REDIS_NEW_SONG_CACHE}, keys = {"type", "number"}, ttl = 10, unit = TimeUnit.SECONDS)
    public Object newSong(Job.request request){
        Job.response response = new Job.response(){{
            setJobName("B");
            setType(request.getType());
        }};
        return response;
    }

}
