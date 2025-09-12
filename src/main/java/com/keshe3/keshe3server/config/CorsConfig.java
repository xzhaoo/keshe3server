package com.keshe3.keshe3server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 配置跨域资源共享(CORS)的映射规则
     * @Override 表示重写父类中的 addCorsMappings 方法
     * @param registry CORS注册配置对象，用于添加跨域映射规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 添加跨域映射规则，"/**"表示匹配所有URL路径
        registry.addMapping("/**")
                // 设置允许跨域请求的源地址，这里指定为本地8081端口
                .allowedOrigins("http://localhost:8081")
                // 设置允许跨域请求的HTTP方法，"*"表示允许所有方法
                .allowedMethods("*")
                // 设置允许跨域请求的请求头，"*"表示允许所有请求头
                .allowedHeaders("*")
                // 是否允许携带认证信息(如cookies)
                .allowCredentials(true);
    }
}