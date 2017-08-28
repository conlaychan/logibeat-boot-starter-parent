package com.logibeat.cloud.boot.mybatis.model;

import com.google.common.collect.BoundType;

/**
 * 二次封装 com.google.common.collect
 */
public class Range<C extends Comparable> {
    private final com.google.common.collect.Range<C> range;

    private Range(com.google.common.collect.Range<C> range){
        this.range = range;
    }

    public com.google.common.collect.Range<C> getGuavaRange(){
        return this.range;
    }

    public static <C extends Comparable<?>> Range<C> openClosed(C lower, C upper){
        return new Range<>(com.google.common.collect.Range.openClosed(lower, upper));
    }

    public static <C extends Comparable<?>> Range<C> open(C lower, C upper){
        return new Range<>(com.google.common.collect.Range.open(lower, upper));
    }

    public static <C extends Comparable<?>> Range<C> closed(C lower, C upper){
        return new Range<>(com.google.common.collect.Range.closed(lower, upper));
    }

    public static <C extends Comparable<?>> Range<C> closedOpen(C lower, C upper){
        return new Range<>(com.google.common.collect.Range.closedOpen(lower, upper));
    }

    public static <C extends Comparable<?>> Range<C> range(C lower, BoundType lowerType, C upper, BoundType upperType){
        return new Range<>(com.google.common.collect.Range.range(lower, lowerType, upper, upperType));
    }

    public static <C extends Comparable<?>> Range<C> lessThan(C endpoint){
        return new Range<>(com.google.common.collect.Range.lessThan(endpoint));
    }

    public static <C extends Comparable<?>> Range<C> atMost(C endpoint){
        return new Range<>(com.google.common.collect.Range.atMost(endpoint));
    }

    public static <C extends Comparable<?>> Range<C> greaterThan(C endpoint){
        return new Range<>(com.google.common.collect.Range.greaterThan(endpoint));
    }

    public static <C extends Comparable<?>> Range<C> atLeast(C endpoint){
        return new Range<>(com.google.common.collect.Range.atLeast(endpoint));
    }

    public boolean hasLowerBound() {
        return range.hasLowerBound();
    }

    public boolean hasUpperBound() {
        return range.hasUpperBound();
    }

    public C getLowerEndpoint(){
        return range.lowerEndpoint();
    }

    public C getUpperEndpoint(){
        return range.upperEndpoint();
    }

    public String getLowerBoundType(){
        return range.lowerBoundType().name();
    }

    public String getUpperBoundType(){
        return range.upperBoundType().name();
    }
}
