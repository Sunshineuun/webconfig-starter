package com.qiusm.config.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiushengming
 */
@ConfigurationProperties(prefix = "qiusm.web.config")
public class WebConfigProperties {
    private String appName = "default";

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
