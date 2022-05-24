package com.qiusm.config.starter;

@FunctionalInterface
public interface BuildRegistry<T> {
    void register(T registry);
}
