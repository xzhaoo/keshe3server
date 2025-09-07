package com.keshe3.keshe3server.interceptor;

import com.keshe3.keshe3server.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果是OPTIONS请求，则放行
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // 获取请求头中的Token
        String token = request.getHeader("Authorization");

        // 如果是登录请求，放行
        if (request.getRequestURI().contains("/user/login") ||
                request.getRequestURI().contains("/user/register")) {
            return true;
        }

        // 验证Token
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtUtils.validateToken(token)) {
                // Token有效，将用户信息存入请求属性中
                request.setAttribute("userId", jwtUtils.getUserIdFromToken(token));
                request.setAttribute("username", jwtUtils.getUsernameFromToken(token));
                request.setAttribute("userPermission", jwtUtils.getUserPermissionFromToken(token));
                return true;
            }
        }

        // Token无效，返回未授权错误
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"code\": 401, \"data\": \"Token无效或已过期\"}");
        return false;
    }
}