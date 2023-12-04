package icu.uun.starter.druid;

import com.alibaba.druid.pool.DruidDataSourceStatLogger;
import com.alibaba.druid.pool.DruidDataSourceStatLoggerAdapter;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.fastjson.JSONObject;
import icu.uun.base.druid.DruidSqlMonitor;
import icu.uun.starter.cat.InetUtils;
import icu.uun.starter.util.CommonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qiushengming
 */
@Slf4j
@Component
//@ConditionalOnBean(KafkaTemplate.class)
public class DahaStatLogger extends DruidDataSourceStatLoggerAdapter implements DruidDataSourceStatLogger {
    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;
    private final String ip;
    @Value("${spring.application.name}")
    private String applicationName;
    public static String TOPIC;

    @Value("${uun.monitor.druidSql.topic:MONITOR_DRUID_SQL}")
    public void setTopic(String topic) {
        TOPIC = topic;
    }


    public DahaStatLogger() {
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
                    kafkaTemplate.send(TOPIC, JSONObject.toJSONString(druidSqlMonitor));
                } catch (Exception e) {
                    log.error("druid sql monitor: {}", CommonUtil.toStackTraceStr(e));
                }
            }
        }
    }
}