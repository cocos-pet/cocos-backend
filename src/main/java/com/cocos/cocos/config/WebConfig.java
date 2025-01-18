package com.cocos.cocos.config;

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
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "https://www.cocos.r-e.kr/", "https://cocos-pet.kr")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
