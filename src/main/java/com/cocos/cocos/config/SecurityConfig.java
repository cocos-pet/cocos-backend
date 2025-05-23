package com.cocos.cocos.config;

import com.cocos.cocos.auth.CustomAccessDeniedHandler;
import com.cocos.cocos.auth.CustomJwtAuthenticationEntryPoint;
import com.cocos.cocos.auth.JwtAuthenticationFilter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Value("${api.prefix}")
    private String apiPrefix;

    private String[] WHITE_LIST_URL;
    private String[] BLACK_LIST_GET_METHOD_URL_IN_WHITE_LIST;
    private String[] BLACK_LIST_POST_METHOD_URL_IN_WHITE_LIST;
    private String[] BLACK_LIST_PATCH_METHOD_URL_IN_WHITE_LIST;
    private String[] BLACK_LIST_DELETE_METHOD_URL_IN_WHITE_LIST;

    @PostConstruct
    private void initUrlList() {
        WHITE_LIST_URL = new String[]{
                "/swagger-ui/**",
                "/v3/api-docs/**",
                apiPrefix + "/symptoms",
                apiPrefix + "/diseases",
                apiPrefix + "/bodies",
                apiPrefix + "/breeds",
                apiPrefix + "/animals",
                apiPrefix + "/posts/**",
                apiPrefix + "/comments/**",
                apiPrefix + "/members/login",
                //TODO: 클라쌤들의 개발을 위해 임시로 추가
                apiPrefix + "/hospitals/**",
        };

        BLACK_LIST_GET_METHOD_URL_IN_WHITE_LIST = new String[]{
                apiPrefix + "/posts/members",
                apiPrefix + "/comments/members",
        };

        BLACK_LIST_POST_METHOD_URL_IN_WHITE_LIST = new String[]{
                apiPrefix + "/posts",
                apiPrefix + "/comments/{postId}",
                apiPrefix + "/comments/sub/{commentId}",
        };

        BLACK_LIST_PATCH_METHOD_URL_IN_WHITE_LIST = new String[]{

        };

        BLACK_LIST_DELETE_METHOD_URL_IN_WHITE_LIST = new String[]{
                apiPrefix + "/posts/{postId}",
                apiPrefix + "/comments/{commentId}",
                apiPrefix + "/comments/sub/{subCommentId}",
        };
    }

    @Bean
    @Profile("dev")
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception ->
                {
                    exception.authenticationEntryPoint(customJwtAuthenticationEntryPoint);
                    exception.accessDeniedHandler(customAccessDeniedHandler);
                });

        http.authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.GET,
                                    BLACK_LIST_GET_METHOD_URL_IN_WHITE_LIST)
                            .authenticated();
                    auth.requestMatchers(HttpMethod.POST,
                                    BLACK_LIST_POST_METHOD_URL_IN_WHITE_LIST)
                            .authenticated();
                    auth.requestMatchers(HttpMethod.PATCH,
                                    BLACK_LIST_PATCH_METHOD_URL_IN_WHITE_LIST)
                            .authenticated();
                    auth.requestMatchers(HttpMethod.DELETE,
                                    BLACK_LIST_DELETE_METHOD_URL_IN_WHITE_LIST)
                            .authenticated();
                    auth.requestMatchers(WHITE_LIST_URL).permitAll();
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.cors(cors -> cors.configurationSource(CorsConfig.corsConfigurationSource()));

        return http.build();
    }

    @Bean
    @Profile({"local", "test"})
    SecurityFilterChain securityFilterChainLocal(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception ->
                {
                    exception.authenticationEntryPoint(customJwtAuthenticationEntryPoint);
                    exception.accessDeniedHandler(customAccessDeniedHandler);
                });
        http.authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.GET,
                                    BLACK_LIST_GET_METHOD_URL_IN_WHITE_LIST)
                            .authenticated();
                    auth.requestMatchers(HttpMethod.POST,
                                    BLACK_LIST_POST_METHOD_URL_IN_WHITE_LIST)
                            .authenticated();
                    auth.requestMatchers(HttpMethod.PATCH,
                                    BLACK_LIST_PATCH_METHOD_URL_IN_WHITE_LIST)
                            .authenticated();
                    auth.requestMatchers(HttpMethod.DELETE,
                                    BLACK_LIST_DELETE_METHOD_URL_IN_WHITE_LIST)
                            .authenticated();
                    auth.requestMatchers(WHITE_LIST_URL).permitAll();
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
