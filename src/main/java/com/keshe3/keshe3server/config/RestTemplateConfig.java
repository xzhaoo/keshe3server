package com.keshe3.keshe3server.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * 创建并配置RestTemplate Bean实例
     * RestTemplate是Spring框架提供的一个用于同步客户端HTTP访问的类，
     * 它简化了与HTTP服务的通信，并处理了大量的细节
     *
     * @return 配置好的RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        // 使用自定义的HTTP请求工厂创建RestTemplate实例
        // 这样可以配置请求超时、重试策略等HTTP相关参数
        return new RestTemplate(httpRequestFactory());
    }

    /**
     * 创建并配置一个HttpComponentsClientHttpRequestFactory Bean
     * 该Bean用于支持Spring RestTemplate使用Apache HttpClient作为底层HTTP客户端
     *
     * @return 配置好的HttpComponentsClientHttpRequestFactory实例
     */
    @Bean
    public HttpComponentsClientHttpRequestFactory httpRequestFactory() {
        // 创建一个新的HttpComponentsClientHttpRequestFactory实例
        // 使用自定义配置的httpClient()作为底层HTTP客户端
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    /**
     * 创建并配置一个CloseableHttpClient Bean实例
     * 该方法配置了连接池和请求超时时间等参数
     *
     * @return 配置好的CloseableHttpClient实例
     */
    @Bean
    public CloseableHttpClient httpClient() {
        // 连接池配置
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        // 最大连接数
        connectionManager.setMaxTotal(200);
        // 每个路由的最大连接数
        connectionManager.setDefaultMaxPerRoute(50);

        // 请求配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30000) // 连接超时时间
                .setSocketTimeout(60000)  // 读取超时时间
                .setConnectionRequestTimeout(10000) // 从连接池获取连接的超时时间
                .build();

        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}