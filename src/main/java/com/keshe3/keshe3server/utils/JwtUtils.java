package com.keshe3.keshe3server.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${jwt.secret:defaultSecretKey}")
    private String secret;

    @Value("${jwt.expiration:2592000000}") // 默认30天
    private Long expiration;

    /**
     * 获取用于签名的密钥
     * 该方法使用HMAC-SHA算法生成一个密钥，用于数字签名验证
     * @return 返回一个SecretKey对象，可用于签名操作
     */
    private SecretKey getSigningKey() {
        // 使用预定义的secret字符串生成HMAC-SHA密钥
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成Token
     * 该方法用于根据用户信息生成JWT（JSON Web Token）令牌
     * @param userId 用户唯一标识
     * @param username 用户名
     * @param userPermission 用户权限
     * @return 返回生成的JWT令牌字符串
     */
    public String generateToken(String userId, String username, String userPermission) {
        // 创建一个HashMap用于存储JWT的声明（claims）
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("userPermission", userPermission);

        // 使用Jwts构建器创建JWT令牌
        return Jwts.builder()
                // 设置声明
                .setClaims(claims)
                // 设置主题（subject）为用户ID
                .setSubject(userId)
                // 设置签发时间为当前时间
                .setIssuedAt(new Date())
                // 设置过期时间（当前时间 + 有效期）
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                // 使用HS512算法和密钥进行签名
                .signWith(SignatureAlgorithm.HS512, secret)
                // compact()方法完成JWT的构建并返回字符串形式的令牌
                .compact();
    }

    /**
     * 验证Token的方法
     * 该方法用于验证传入的JWT令牌是否有效
     * @param token 需要验证的JWT令牌字符串
     * @return 如果令牌有效返回true，无效返回false
     */
    public boolean validateToken(String token) {
        try {
            // 使用JWT解析器构建器设置签名密钥
            Jwts.parserBuilder()
                    // 设置用于验证令牌签名的密钥
                    .setSigningKey(secret)
                    // 构建解析器实例
                    .build()
                    // 解析并验证令牌，如果令牌无效会抛出异常
                    .parseClaimsJws(token);
            // 如果解析成功，说明令牌有效，返回true
            return true;
        } catch (Exception e) {
            // 捕获任何解析过程中可能出现的异常（如签名不匹配、令牌过期等）
            // 出现异常说明令牌无效，返回false
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     * 该方法使用JWT解析token，提取其中的用户身份标识信息
     *
     * @param token JWT格式的令牌字符串
     * @return 用户ID，作为token的主题(subject)返回
     */
    public String getUserIdFromToken(String token) {
        // 使用JWT解析器设置签名密钥并解析token
        Claims claims = Jwts.parser()
                .setSigningKey(secret)    // 设置用于验证token的签名密钥
                .parseClaimsJws(token)    // 解析token并获取Claims主体
                .getBody();               // 获取token中的Claims负载部分
        // 返回token中存储的用户身份标识（subject）
        return claims.getSubject();
    }

    /**
     * 从Token中获取用户名
     * 该方法用于解析JWT令牌并提取其中的用户名信息
     *
     * @param token JWT令牌字符串
     * @return 返回令牌中存储的用户名，如果不存在则返回null
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.get("username");
    }

    /**
     * 从Token中获取用户权限
     * 该方法通过解析JWT令牌，提取其中的用户权限信息
     *
     * @param token JWT令牌字符串
     * @return 返回用户权限信息，以字符串形式表示
     */
    public String getUserPermissionFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.get("userPermission");
    }

    /**
     * 从Token中获取Claims
     * 该方法用于解析JWT令牌并提取其中的声明(Claims)信息
     * Claims是JWT令牌中存储的声明信息的载体，包含了用户的身份信息、权限等数据
     *
     * @param token JWT格式的字符串令牌
     * @return Claims 包含令牌中所有声明信息的对象
     */
    public Claims getAllClaimsFromToken(String token) {
        // 使用Jwts.parserBuilder()创建一个解析器构建器
        // .setSigningKey(secret) 设置用于验证签名的密钥
        // .build() 构建出解析器实例
        // .parseClaimsJws(token) 解析并验证令牌，如果签名无效会抛出异常
        // .getBody() 获取令牌中的声明(Claims)主体部分
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}