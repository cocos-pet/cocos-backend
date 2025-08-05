package com.cocos.cocos.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CorsConfig {
    //ToDo: final 키워드 붙일 곳 붙여야 할 듯
    public static CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //리소스를 허용할 URL 지정
        ArrayList<String> allowedOriginPatterns = new ArrayList<>();
        allowedOriginPatterns.add("http://localhost:5173");
        allowedOriginPatterns.add("https://www.cocos.r-e.kr/");
        allowedOriginPatterns.add("https://www.cocos-dev.r-e.kr");
        allowedOriginPatterns.add("https://www.cocos-pet.kr/");
        allowedOriginPatterns.add("https://cocos-frontend-seven.vercel.app");
        configuration.setAllowedOrigins(allowedOriginPatterns);

        //허용하는 HTTP METHOD 지정
        ArrayList<String> allowedHttpMethods = new ArrayList<>();
        allowedHttpMethods.add("GET");
        allowedHttpMethods.add("POST");
        allowedHttpMethods.add("PUT");
        allowedHttpMethods.add("DELETE");
        allowedHttpMethods.add("PATCH");
        allowedHttpMethods.add("OPTIONS");
        configuration.setAllowedMethods(allowedHttpMethods);

        configuration.setAllowedHeaders(Collections.singletonList("*"));
//        configuration.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));

        //인증, 인가를 위한 credentials 를 TRUE로 설정
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
