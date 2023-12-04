# 0.0.5 版本
## druid sql 监控数据持久化
将监控数据推送到kafka。再由其它服务进行消费。目前，是将数据存储到Hbase中。
数据推送间隔由`time-between-log-stats-millis` 控制。