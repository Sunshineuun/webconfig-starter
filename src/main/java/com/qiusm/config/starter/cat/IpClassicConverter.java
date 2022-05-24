package com.qiusm.config.starter.cat;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * 用于获取本机IP地址
 *
 * @author qiushengming
 */
public class IpClassicConverter extends ClassicConverter {
    private static final Logger log = LoggerFactory.getLogger(IpClassicConverter.class);
    public static String webIP;

    public IpClassicConverter() {
    }

    @Override
    public String convert(ILoggingEvent event) {
        return webIP;
    }

    static {
        InetAddress ip = InetUtils.findFirstNonLoopbackAddress();
        webIP = ip != null ? ip.getHostAddress() : null;
    }
}