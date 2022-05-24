package com.qiusm.config.starter.util;

import com.alibaba.fastjson.JSONObject;
import com.qiusm.config.starter.servlet.CacheHttpServletRequestWrapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class ServletUtils {
    private static final Logger log = LoggerFactory.getLogger(ServletUtils.class);

    public ServletUtils() {
    }

    public static Map<String, String> parseHeaderMap(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> names = request.getHeaderNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = request.getHeader(name);
            headerMap.put(name, value);
        }

        return headerMap;
    }

    public static void writerResponseBody(HttpServletResponse response, String responseBody) {
        try {
            response.getWriter().append(responseBody);
        } catch (IOException var3) {
            throw new RuntimeException("输出[" + responseBody + "]给[" + response + "]时发生异常:" + var3.getMessage(), var3);
        }
    }

    public static void setResponseHeader(HttpServletResponse response, Map<String, String> responseHeader) {
        for (Entry<String, String> entry : responseHeader.entrySet()) {
            response.addHeader(entry.getKey(), entry.getValue());
        }

    }

    public static String getBodyByRequest(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;

        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = "";

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException var17) {
            var17.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException var16) {
                    var16.printStackTrace();
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }

        }

        return sb.toString();
    }

    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-real-ip");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip.startsWith("unknown")) {
            ip = ip.substring(ip.indexOf("unknown") + "unknown".length());
        }

        ip = ip.trim();
        if (ip.startsWith(",")) {
            ip = ip.substring(1);
        }

        if (ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }

        return ip;
    }

    public static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
        Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        Assert.state(true, "Could not find current HttpServletRequest");
        return servletRequest;
    }

    public static String getParameterMap(HttpServletRequest req) {
        HttpServletRequest childReq = null;
        if (Stream.of(req.getClass().getMethods()).anyMatch((e) -> {
            return "getRequest".equals(e.getName()) && e.getParameterTypes().length == 0;
        })) {
            try {
                childReq = (HttpServletRequest) req.getClass().getMethod("getRequest").invoke(req, (Object[]) null);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var3) {
                var3.printStackTrace();
            }
        }

        if (req instanceof CacheHttpServletRequestWrapper) {
            return ((CacheHttpServletRequestWrapper) req).getBody();
        } else if (childReq != null) {
            if (childReq instanceof CacheHttpServletRequestWrapper) {
                return ((CacheHttpServletRequestWrapper) childReq).getBody();
            } else {
                return childReq instanceof HttpServletRequestWrapper
                        && ((HttpServletRequestWrapper) childReq).getRequest() instanceof HttpServletRequestWrapper
                        && ((HttpServletRequestWrapper) ((HttpServletRequestWrapper) childReq).getRequest()).getRequest() instanceof CacheHttpServletRequestWrapper
                        ? ((CacheHttpServletRequestWrapper) ((HttpServletRequestWrapper) ((HttpServletRequestWrapper) childReq).getRequest()).getRequest()).getBody()
                        : JSONObject.toJSONString(req.getParameterMap());
            }
        } else {
            return JSONObject.toJSONString(req.getParameterMap());
        }
    }

    public static void eBaasLogPrint(HttpServletRequest e) {
        String method = e.getMethod();
        String uri = e.getRequestURI();
        String params = null;
        String loginAccountInfo = null;
        if (!uri.contains("upload")) {
            String authorization = e.getHeader("Authorization");
            if (StringUtils.isNotBlank(authorization)
                    && authorization.replaceFirst("bearer", "").split("\\.").length == 3) {
                loginAccountInfo = new String(Base64.decodeBase64(authorization.replaceFirst("bearer", "").trim().split("\\.")[1]));
            }

            params = getParameterMap(e);
        }

        if (StringUtils.isNotBlank(loginAccountInfo)) {
            log.info("@@请求地址:{}, 请求方式:{},用户:{}, \n参数:{}", uri, method, loginAccountInfo, params);
        } else {
            log.info("@@请求地址:{}, 请求方式:{},memberId:{},merchantId:{},operatorId:{}, \n参数:{}", uri, method, e.getHeader("memberId"), e.getHeader("merchantId"), e.getHeader("operatorId"), params);
        }

    }
}
