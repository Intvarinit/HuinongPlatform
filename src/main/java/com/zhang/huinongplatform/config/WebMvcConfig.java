package com.zhang.huinongplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问 /upload/** 映射到项目根目录下的 upload 目录
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:upload/");
    }
}