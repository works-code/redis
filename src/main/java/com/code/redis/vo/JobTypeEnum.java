package com.code.redis.vo;

public enum JobTypeEnum {
    A("type:A"),
    B("type:B"),
    C("type:C");

    String value = "";

    JobTypeEnum(String value) {
        this.value = value;
    }
}
