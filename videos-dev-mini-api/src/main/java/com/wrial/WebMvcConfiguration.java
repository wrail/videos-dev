package com.wrial;

import com.wrial.interceptor.MyLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {


    /*
    配置资源映射器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**")
                //Swagger路径
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:D:/dev/videos/");
    }


    /*
    配置自定义登录拦截器
     */
    @Bean
    public MyLoginInterceptor myLoginInterceptor() {
        return new MyLoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myLoginInterceptor())
                .addPathPatterns("/user/**")
                .addPathPatterns("/video/upload", "/video/uploadCover",
                        "/video/userLike", "/video/userUnLike",
                        "/video/saveComment")
                .addPathPatterns("/bgm/**")
                .excludePathPatterns("/user/queryPublisher");
    }
}
