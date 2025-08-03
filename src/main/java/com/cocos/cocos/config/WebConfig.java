package com.cocos.cocos.config;

import com.cocos.cocos.common.interceptor.HibernateQueryCountInterceptor;
import com.cocos.cocos.util.PetProblemEnumConverter;
import com.cocos.cocos.util.PostSortCriteriaEnumConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final HibernateQueryCountInterceptor hibernateQueryCountInterceptor;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PostSortCriteriaEnumConverter());
        registry.addConverter(new PetProblemEnumConverter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:5173/",
                        "https://www.cocos.r-e.kr/",
                        "https://www.cocos-pet.kr/"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hibernateQueryCountInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**"
                );
    }
}
