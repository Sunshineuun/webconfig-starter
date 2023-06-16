package icu.uun.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author qiushengming
 */
@Component
@ConfigurationProperties(prefix = "uun.web.config")
public class WebConfigProperties {
    private String appName = "default";

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
