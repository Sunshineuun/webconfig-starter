package icu.uun.starter.phoenix;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface PhoenixBaseMapper<T> extends BaseMapper<T> {
    int upsert(T entity);
}