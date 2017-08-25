package com.logibeat.cloud.boot.mybatis.model;

public class PageInfo {
    private final Integer offset;
    private final Integer limit;

    public static PageInfo of(Integer pageNo, Integer size) {
        return new PageInfo(pageNo, size);
    }

    public PageInfo(Integer pageNo, Integer size) {
        pageNo = pageNo == null ? 1 : pageNo;
        size = size == null ? 20 : size;
        limit = size > 0 ? size : 20;
        int offset = (pageNo - 1) * size;
        this.offset = offset > 0 ? offset : 0;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }
}
