package com.ls.supstar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 * 该类用于配置Spring Boot应用的全局跨域资源共享（CORS）策略。
 * 通过实现WebMvcConfigurer接口并重写addCorsMappings方法，可以自定义CORS规则。
 */
@Configuration // 标记该类为配置类，Spring Boot会自动加载并应用该配置
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置CORS规则
        registry.addMapping("/**") // 匹配所有路径，即对所有请求应用CORS规则
                // 允许跨域请求携带凭证信息（如Cookies、HTTP认证等）
                .allowCredentials(true)
                // 允许哪些域名访问资源。使用allowedOriginPatterns而不是allowedOrigins，
                // 因为allowedOrigins("*")与allowCredentials(true)冲突。
                // allowedOriginPatterns支持通配符，且可以与allowCredentials(true)一起使用。
                .allowedOriginPatterns("*")
                // 允许的HTTP方法，包括GET、POST、PUT、DELETE和OPTIONS
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许所有请求头
                .allowedHeaders("*")
                // 暴露所有响应头，使客户端可以访问这些头信息
                .exposedHeaders("*");
    }
}