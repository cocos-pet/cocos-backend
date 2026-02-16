package com.cocos.cocos.external.s3;

import lombok.Getter;

@Getter
public enum S3BucketType {

    APP_DATA("aws.app-data-s3-bucket-name"),
    MEMBER_DATA("aws.member-data-s3-bucket-name");

    private final String propertyKey;

    S3BucketType(String propertyKey) {
        this.propertyKey = propertyKey;
    }

}
