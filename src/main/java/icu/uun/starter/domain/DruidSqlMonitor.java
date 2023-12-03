package icu.uun.starter.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiushengming
 */
@NoArgsConstructor
@Data
@TableName(value = "MONITOR.DRUID_SQL_MONITOR")
public class DruidSqlMonitor {
    private String id;
    private String ip;
    private String dataSource;
    private String sql;
    private Integer executeCount;
    private Integer errorCount;

    private Integer totalTime;
    private Long lastTime;
    private Integer maxTimespan;
    private String lastError;
    private Integer effectedRowCount;

    private Integer fetchRowCount;
    private Long maxTimespanOccurTime;
    private Integer batchSizeMax;
    private Integer batchSizeTotal;
    private Integer concurrentMax;

    private Integer runningCount;
    private String name;
    private String file;
    private String lastErrorMessage;
    private String lastErrorClass;
    private String lastErrorStackTrace;
    private String lastErrorTime;

    private String dbType;
//    private String url;
    private Integer inTransactionCount;

    private String histogram;
    private String lastSlowParameters;
    private Integer resultSetHoldTime;
    private Integer executeAndResultSetHoldTime;
    private String fetchRowCountHistogram;
    private String effectedRowCountHistogram;
    private String executeAndResultHoldTimeHistogram;
    private Integer effectedRowCountMax;
    private Integer fetchRowCountMax;
    private Integer clobOpenCount;
    private Integer blobOpenCount;
    private Integer readStringLength;
    private Integer readBytesLength;
    private Integer inputStreamOpenCount;
    private Integer readerOpenCount;

    private Long hash;
}
