package com.code.redis.vo;

import lombok.Data;

@Data
public class Struct {

    @Data
    public static class SortedSet{
        Double score = 0D;
        String value = "";
    }
}
