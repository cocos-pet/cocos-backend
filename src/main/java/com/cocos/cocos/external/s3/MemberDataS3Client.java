package com.cocos.cocos.external.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.awscore.presigner.PresignRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.Duration;

@Component
public class MemberDataS3Client {

    private final S3Presigner s3Presigner;
    private final String bucketName;

    public MemberDataS3Client(
            S3Presigner s3Presigner,
            @Value("${aws.member-data-s3-bucket-name}") String bucketName
    ) {
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
    }

    public String getPresignedUrl(String fileName) {
        return presign(
                GetObjectPresignRequest.builder()
                        .getObjectRequest(
                                GetObjectRequest.builder()
                                        .bucket(bucketName)
                                        .key(fileName)
                                        .build()
                        )
                        .signatureDuration(Duration.ofMinutes(5))
                        .build()
        );
    }

    public String putPresignedUrl(String fileName) {
        return presign(
                PutObjectPresignRequest.builder()
                        .putObjectRequest(
                                PutObjectRequest.builder()
                                        .bucket(bucketName)
                                        .key(fileName)
                                        .build()
                        )
                        .signatureDuration(Duration.ofMinutes(5))
                        .build()
        );
    }

    public String deletePresignedUrl(String fileName) {
        return presign(
                DeleteObjectPresignRequest.builder()
                        .deleteObjectRequest(
                                DeleteObjectRequest.builder()
                                        .bucket(bucketName)
                                        .key(fileName)
                                        .build()
                        )
                        .signatureDuration(Duration.ofMinutes(5))
                        .build()
        );
    }

    private String presign(PresignRequest request) {
        if (request == null) {
            return null;
        }

        if (request instanceof GetObjectPresignRequest r) {
            return s3Presigner.presignGetObject(r).url().toString();
        }
        if (request instanceof PutObjectPresignRequest r) {
            return s3Presigner.presignPutObject(r).url().toString();
        }
        if (request instanceof DeleteObjectPresignRequest r) {
            return s3Presigner.presignDeleteObject(r).url().toString();
        }

        throw new IllegalArgumentException("Unsupported presign request");
    }
}
