package icu.uun.starter.druid;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
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
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.UnknownHostException;

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
    private final Snowflake secondSnow = IdUtil.getSnowflake(getWorkId(), getDataCenterId());


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
                    druidSqlMonitor.setDataSource(applicationName);
                    druidSqlMonitor.setId(secondSnow.nextIdStr());
                    druidSqlMonitor.setIp(this.ip);
                    druidSqlMonitor.setUpdateTime(DateUtil.now());
                    kafkaTemplate.send(TOPIC, JSONObject.toJSONString(druidSqlMonitor));
                } catch (Exception e) {
                    log.error("druid sql monitor: {}", CommonUtil.toStackTraceStr(e));
                }
            }
        }
    }

    /**
     * workId使用IP生成
     * @return workId
     */
    private static Long getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for (int b : ints) {
                sums = sums + b;
            }
            return (long) (sums % 32);
        }
        catch (UnknownHostException e) {
            // 失败就随机
            return RandomUtils.nextLong(0, 31);
        }
    }


    /**
     * dataCenterId使用hostName生成
     * @return dataCenterId
     */
    private static Long getDataCenterId() {
        try {
            String hostName = SystemUtils.getHostName();
            int[] ints = StringUtils.toCodePoints(hostName);
            int sums = 0;
            for (int i: ints) {
                sums = sums + i;
            }
            return (long) (sums % 32);
        }
        catch (Exception e) {
            // 失败就随机
            return RandomUtils.nextLong(0, 31);
        }
    }
}