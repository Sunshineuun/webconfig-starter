package com.qiusm.config.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author qiushengming
 */
@Component
@ConfigurationProperties(prefix = "qiusm.web.config.logstash")
public class LogStashProperties {
    private Integer enable;
    private String host;

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
