package com.cocos.cocos.config;

import com.cocos.cocos.util.LocationTypeEnumConverter;
import com.cocos.cocos.util.PetProblemEnumConverter;
import com.cocos.cocos.util.SortCriteriaEnumConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new SortCriteriaEnumConverter());
        registry.addConverter(new PetProblemEnumConverter());
        registry.addConverter(new LocationTypeEnumConverter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173/", "https://www.cocos.r-e.kr/", "https://www.cocos-pet.kr/")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowCredentials(true);
    }
}
