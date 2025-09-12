package com.keshe3.keshe3server.config;

import com.keshe3.keshe3server.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * 添加拦截器配置
     * 该方法用于配置Spring MVC的拦截器，用于在请求处理前进行拦截处理
     * @param registry 拦截器注册器，用于注册和配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器，并配置拦截规则
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有路径
                .addPathPatterns("/**")
                // 排除登录和注册接口，允许匿名访问
                .excludePathPatterns("/user/login", "/user/register");
    }

    /**
     * 重写添加资源处理器的方法，用于配置静态资源访问
     * @param registry 资源处理器注册器，用于注册静态资源处理器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加资源处理器，配置"/uploads/**"路径的请求映射到本地文件系统
        registry.addResourceHandler("/uploads/**")
                // 设置资源实际存储位置，指向uploadDir变量指定的目录
                .addResourceLocations("file:" + uploadDir + "/");
    }
}