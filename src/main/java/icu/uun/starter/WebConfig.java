package icu.uun.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("favicon.ico").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        if (this.buildResourceHandlerRegistry != null) {
            this.buildResourceHandlerRegistry.register(registry);
        }

    }
}
