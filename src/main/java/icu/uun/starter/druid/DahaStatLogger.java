package icu.uun.starter.druid;

import com.alibaba.druid.pool.DruidDataSourceStatLogger;
import com.alibaba.druid.pool.DruidDataSourceStatLoggerAdapter;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.fastjson.JSONObject;
import icu.uun.starter.cat.InetUtils;
import icu.uun.starter.domain.DruidSqlMonitor;
import icu.uun.starter.mapper.DruidSqlMonitorMapper;
import icu.uun.starter.util.CommonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author qiushengming
 */
@Slf4j
@Component
public class DahaStatLogger extends DruidDataSourceStatLoggerAdapter implements DruidDataSourceStatLogger {

    private final DruidSqlMonitorMapper druidSqlMonitorMapper;
    private final String ip;
    @Value("${spring.application.name}")
    private String applicationName;

    public DahaStatLogger(DruidSqlMonitorMapper druidSqlMonitorMapper) {
        this.druidSqlMonitorMapper = druidSqlMonitorMapper;
        this.ip = InetUtils.findFirstNonLoopbackAddress().getHostName();
    }

    @SneakyThrows
    @Override
    public void log(DruidDataSourceStatValue statValue) {
        //有执行sql的话 只显示sql语句
        if (statValue.getSqlList().size() > 0) {
            for (JdbcSqlStatValue sqlStat : statValue.getSqlList()) {
                try {
                    DruidSqlMonitor druidSqlMonitor = JSONObject
                            .parseObject(JSONObject.toJSONString(sqlStat.getData()), DruidSqlMonitor.class);
                    String id = String.format("%s_%s_%s", applicationName, druidSqlMonitor.getHash().toString(),
                            druidSqlMonitor.getId());
                    druidSqlMonitor.setId(id);
                    druidSqlMonitor.setIp(this.ip);
                    druidSqlMonitorMapper.upsert(druidSqlMonitor);
                } catch (Exception e) {
                    log.error("druid sql monitor: {}", CommonUtil.toStackTraceStr(e));
                }
            }
        }
    }
}