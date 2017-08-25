package com.logibeat.cloud.boot.mybatis.model;

import com.google.common.collect.Range;
import lombok.Data;

import java.util.Date;

@Data
public class EntityCriteria {
    private Date updateAt;
    private Date createAt;
    private Range<Date> updateRange;
    private Range<Date> createRange;
}
