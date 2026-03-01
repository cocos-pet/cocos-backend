package com.cocos.cocos.external.s3;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.enums.message.FailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.DeleteObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class S3PresignClient {

    private static final Duration DEFAULT_EXPIRE = Duration.ofMinutes(5);

    private final S3Presigner presigner;
    private final S3BucketResolver bucketResolver;

    public String get(final S3BucketType bucketType, final String key) {
        if (key == null || key.isBlank()) {
            return "";
        }

        final String bucket = bucketResolver.resolve(bucketType);

        final GetObjectPresignRequest request =
                GetObjectPresignRequest.builder()
                        .signatureDuration(DEFAULT_EXPIRE)
                        .getObjectRequest(b -> b
                                .bucket(bucket)
                                .key(key)
                        )
                        .build();

        return presigner.presignGetObject(request).url().toString();
    }

    public String put(final S3BucketType bucketType, final String key) {
        validateKey(key);

        final String bucket = bucketResolver.resolve(bucketType);

        final PutObjectPresignRequest request =
                PutObjectPresignRequest.builder()
                        .signatureDuration(DEFAULT_EXPIRE)
                        .putObjectRequest(b -> b
                                .bucket(bucket)
                                .key(key)
                        )
                        .build();


        return presigner.presignPutObject(request).url().toString();
    }

    public String delete(final S3BucketType bucketType, final String key) {
        validateKey(key);

        final String bucket = bucketResolver.resolve(bucketType);

        final DeleteObjectPresignRequest request =
                DeleteObjectPresignRequest.builder()
                        .signatureDuration(DEFAULT_EXPIRE)
                        .deleteObjectRequest(b -> b
                                .bucket(bucket)
                                .key(key)
                        )
                        .build();


        return presigner.presignDeleteObject(request).url().toString();
    }

    private void validateKey(final String key) {
        if (key == null || key.isBlank()) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_S3_OBJECT_KEY);
        }
    }
}
