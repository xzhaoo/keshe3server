package com.keshe3.keshe3server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 配置消息代理，这是一个重写的方法，来自WebSocketMessageBrokerConfigurer接口
     * @param config 消息代理注册表，用于配置消息代理的各种属性
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 配置消息代理，启用一个简单的基于内存的消息代理
        // 该代理将处理以"/topic"为前缀的目标地址
        config.enableSimpleBroker("/topic");
        // 设置应用程序目的地前缀，所有以"/app"开头的消息将被路由到带有@MessageMapping注解的方法
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * 注册STOMP端点配置
     * 该方法用于配置WebSocket的连接端点，允许客户端连接到WebSocket服务器
     *
     * @param registry STOMP端点注册器，用于注册WebSocket端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 添加一个名为"/ws"的WebSocket端点
        // 设置允许所有来源的跨域请求
        // 启用SockJS支持，用于在不支持WebSocket的浏览器中实现WebSocket功能
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}