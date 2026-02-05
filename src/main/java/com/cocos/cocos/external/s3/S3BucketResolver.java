package com.cocos.cocos.external.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3BucketResolver {

    private final Environment environment;

    public String resolve(final S3BucketType type) {
        return environment.getRequiredProperty(type.getPropertyKey());
    }
}
