package com.code.redis.vo;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
public class Job {

    @Data
    public static class request{
        private int number = 0;
        private JobTypeEnum type = JobTypeEnum.A;
    }


    @Data
    public static class response{
        private String jobName;
        private JobTypeEnum type;
        private String cacheDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }

}
