package com.cocos.cocos.external;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.external.login.dto.KakaoAccessTokenResponse;
import com.cocos.cocos.external.login.dto.KakaoInfoResponse;
import com.cocos.cocos.external.login.dto.KakaoUnlinkResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class KakaoLoginClient {

    @Value("${kakao.client-id}")
    private String kakaoClientId;
    @Value("${kakao.redirect-url}")
    private String kakaoRedirectUrl;
    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;

    private static final String ADMIN_KEY_TYPE = "KakaoAK ";

    public String login(final String code) {

        final RestClient restClient = RestClient.create();
        final URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/token")
                .queryParam("code", "{code}")
                .queryParam("client_id", "{client_id}")
                .queryParam("redirect_uri", "{redirect_uri}")
                .queryParam("grant_type", "{grant_type}")
                .encode()
                .buildAndExpand(code, kakaoClientId, kakaoRedirectUrl, "authorization_code")
                .toUri();

        final KakaoAccessTokenResponse kakaoAccessTokenResponse = restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .body(KakaoAccessTokenResponse.class);

        if (kakaoAccessTokenResponse == null || kakaoAccessTokenResponse.accessToken() == null) {
            throw new CocosException(FailMessage.INTERNAL_SERVER_ERROR_KAKAO_ACCESS_TOKEN);
        }

        final KakaoInfoResponse kakaoInfoResponse = restClient.get()
                //ToDo: uri같은 거 yml에 적용 필요
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + kakaoAccessTokenResponse.accessToken())
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .body(KakaoInfoResponse.class);

        if (kakaoInfoResponse == null) {
            throw new CocosException(FailMessage.INTERNAL_SERVER_ERROR_KAKAO_USER_INFO);
        }

        return kakaoInfoResponse.id();
    }

    final public void unlink(final Long id) {
        final RestClient restClient = RestClient.create();
        final URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com/v1/user/unlink")
                .queryParam("target_id_type", "{targetIdType}")
                .queryParam("target_id", "{targetId}")
                .encode()
                .buildAndExpand("user_id", id)
                .toUri();

        final KakaoUnlinkResponse kakaoUnlinkResponse = restClient.post()
                .uri(uri)
                .header("Authorization", ADMIN_KEY_TYPE + kakaoAdminKey)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .body(KakaoUnlinkResponse.class);

        if (kakaoUnlinkResponse == null) {
            throw new CocosException(FailMessage.INTERNAL_SERVER_ERROR_KAKAO_UNLINK);
        }
    }
}
