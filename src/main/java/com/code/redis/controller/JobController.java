package com.code.redis.controller;

import com.code.redis.service.JobService;
import com.code.redis.vo.Job;
import com.code.redis.vo.JobTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

    @Autowired
    private JobService jobService;

    @RequestMapping("/newSong")
    public Object newSong(){
        return jobService.newSong(new Job.request(){{
            setNumber(0);
            setType(JobTypeEnum.C);
        }});
    }

    @RequestMapping("/chart")
    public Object chart(){
        return jobService.chart(new Job.request(){{
            setType(JobTypeEnum.A);
        }});
    }

}
