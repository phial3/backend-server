package org.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * 配置Spring MVC 拦截器等
 *
 * @author phial
 * @since 2019-10-10
 */
@Configuration
public class SpringWebConfig implements WebMvcConfigurer {

    @Resource
    private ThreadPoolTaskExecutor executor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        try {
            // login
            //            registry.addInterceptor(new AnnotationBasedProcessorInterceptor() {
            //                @Override
            //                protected ApplicationContext getApplicationContext(HttpServletRequest request) {
            //                    return context;
            //                }
            //            });

        } catch (Exception e) {

        }
    }


    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(executor);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

    }

    @Override
    public void addFormatters(FormatterRegistry registry) {

    }
}
