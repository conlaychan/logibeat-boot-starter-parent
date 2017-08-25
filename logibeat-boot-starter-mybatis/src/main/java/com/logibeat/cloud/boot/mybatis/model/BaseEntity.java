package com.logibeat.cloud.boot.mybatis.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class BaseEntity implements Serializable{
    private static final long serialVersionUID = -6965751606920939495L;

    private Long id;
    private Date updateAt;
    private Date createAt;
}
