package com.cocos.cocos.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class CloudfrontClient {

    @Value("${aws.cloudfront-url}")
    private String cloudfrontUrl;

    private static final String APP = "app";
    private static final String MEMBER = "member";

    public String getAppCloudfrontUrl(final String image){
        if (image == null) {
            return null;
        }
        return UriComponentsBuilder.fromUri(URI.create(cloudfrontUrl))
                .pathSegment(APP, image)
                .toUriString();
    }

    public String getMemberCloudfrontUrl(final String image) {
        if (image == null) {
            return null;
        }
        return UriComponentsBuilder.fromUri(URI.create(cloudfrontUrl))
                .pathSegment(MEMBER, image)
                .toUriString();
    }
}
