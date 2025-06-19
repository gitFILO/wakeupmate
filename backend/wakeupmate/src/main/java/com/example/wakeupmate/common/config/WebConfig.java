package com.example.wakeupmate.common.config;

import com.example.wakeupmate.common.intercepter.QueryLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final QueryLoggingInterceptor queryLoggingInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins(
                "http://localhost:3000",
                "https://wakeupmate.vercel.app",
                "https://wakeupmate.my",
                "https://www.wakeupmate.my"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .exposedHeaders("Set-Cookie")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(queryLoggingInterceptor)
                .excludePathPatterns("/css/**", "/images/**", "/js/**");
    }

}
