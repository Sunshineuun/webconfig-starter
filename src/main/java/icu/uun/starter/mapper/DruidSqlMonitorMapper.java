package icu.uun.starter.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import icu.uun.starter.domain.DruidSqlMonitor;
import icu.uun.starter.phoenix.PhoenixBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qiushengming
 */
@DS("phoenix")
@Mapper
public interface DruidSqlMonitorMapper extends PhoenixBaseMapper<DruidSqlMonitor> {
}
