package com.cocos.cocos.external;

import com.cocos.cocos.external.login.dto.KakaoAccessTokenResponse;
import com.cocos.cocos.external.login.dto.KakaoInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Component
public class KakaoLoginClient {

    @Value("${kakao.client-id}")
    private String kakaoClientId;
    @Value("${kakao.redirect-url}")
    private String kakaoRedirectUrl;

    public String login(final String code) {

        RestClient restClient = RestClient.create();
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/token")
                .queryParam("code", "{code}")
                .queryParam("client_id", "{client_id}")
                .queryParam("redirect_uri", "{redirect_uri}")
                .queryParam("grant_type", "{grant_type}")
                .encode()
                .buildAndExpand(code, kakaoClientId, kakaoRedirectUrl, "authorization_code")
                .toUri();

        KakaoAccessTokenResponse kakaoAccessTokenResponse = restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .body(KakaoAccessTokenResponse.class);


        log.info(kakaoAccessTokenResponse.accessToken());

        KakaoInfoResponse kakaoInfoResponse = restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + kakaoAccessTokenResponse.accessToken())
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .body(KakaoInfoResponse.class);

        return kakaoInfoResponse.id();
    }
}
