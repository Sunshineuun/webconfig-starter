package icu.uun.starter;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qiushengming
 */
@Configuration
@ComponentScan(basePackages = "icu.uun.starter")
public class WebConfig implements WebMvcConfigurer {
    @Autowired(required = false)
    private BuildRegistry<InterceptorRegistry> buildInterceptorRegistry;

    @Autowired(required = false)
    private BuildRegistry<ResourceHandlerRegistry> buildResourceHandlerRegistry;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        if (this.buildInterceptorRegistry != null) {
            this.buildInterceptorRegistry.register(registry);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("favicon.ico").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        if (this.buildResourceHandlerRegistry != null) {
            this.buildResourceHandlerRegistry.register(registry);
        }
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(stringHttpMessageConverter());
        converters.add(fastJsonHttpMessageConverter());
    }

    /**
     * feat: 当返回string时，会带双引号问题解决，主要是在转换的时候string用了fastjson，所以带了双引号，现在用StringHttpMessageConverter去处理<br>
     * 创建 bean 就没有作用，不知道为啥; fastJsonHttpMessageConverter 方法标记 @Bean 也会导致失败，不确定为啥。 TODO <br>
     * feat: 当返回string时，会带双引号问题解决，spring 已经创建了StringHttpMessageConverter。只要在controller 层标记`@GetMapping(value = "getSendWord", produces = "text/plain")` produces = "text/plain".即可
     *
     */
    @Bean
    public HttpMessageConverter<String> stringHttpMessageConverter() {
        List<MediaType> list = new ArrayList<>();
        list.add(MediaType.APPLICATION_JSON);

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setSupportedMediaTypes(list);
        return stringHttpMessageConverter;
    }

    @Bean
    public HttpMessageConverter<Object> fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setDateFormat("yyy-MM-dd HH:mm:ss");
        config.setCharset(StandardCharsets.UTF_8);
        config.setSerializerFeatures(
//                SerializerFeature.WriteClassName,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty
        );
        SerializeConfig serializeConfig = new SerializeConfig();

        // 过滤器，不使用该序列化的
        // fastjson 使用 PropertyNamingStrategy.SnakeCase 导致swagger页面的api接口显示不了
        NameFilter nameFilter = (object, name, value) -> name;
        serializeConfig.addFilter(UiConfiguration.class, nameFilter);
        serializeConfig.addFilter(SwaggerResource.class, nameFilter);
        serializeConfig.addFilter(MetricsEndpoint.MetricResponse.class, nameFilter);
        serializeConfig.addFilter(EnvironmentEndpoint.EnvironmentDescriptor.class, nameFilter);

        // 驼峰转换
        serializeConfig.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
        config.setSerializeConfig(serializeConfig);

        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(StandardCharsets.UTF_8);

        // 解决中文乱码问题，相当于在Controller上的@RequestMapping中加了个属性produces = "application/json"
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.TEXT_HTML, MediaType.APPLICATION_JSON,
                MediaType.valueOf("application/*+json"), MediaType.ALL));

        return converter;
    }
}
