package com.cocos.cocos.external;

import com.cocos.cocos.config.AwsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
    private final String POST = "post";

    public MemberDataS3Client(@Value("${aws.member-data-s3-bucket-name}") final String bucketName, final AwsConfig awsConfig) {
        this.bucketName = bucketName;
        this.s3Presigner = awsConfig.getPresigner();
    }

    //ToDo: 중복되는 부분에 코드를 줄일 수 있는 방법 필요 (ex: enum을 통해 app, member data구분 등)
    public String getPresignedUrl(final String fileName) {
        if (fileName == null || fileName.equals("")) {
            return null;
        }
        final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        final GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(getObjectRequest)
                .build();

        final PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner
                .presignGetObject(getObjectPresignRequest);
        final String url = presignedGetObjectRequest.url().toString();

        s3Presigner.close();
        return url;
    }

    public String putPresignedUrl(final String fileName) {
        if (fileName == null || fileName.equals("")) {
            return null;
        }

        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        final PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        final PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner
                .presignPutObject(putObjectPresignRequest);
        final String url = presignedPutObjectRequest.url().toString();

        s3Presigner.close();
        return url;
    }

    public String deletePresignedUrl(final String fileName) {
        if (fileName == null || fileName.equals("")) {
            return null;
        }

        final DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        final DeleteObjectPresignRequest deleteObjectPresignRequest = DeleteObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .deleteObjectRequest(deleteObjectRequest)
                .build();

        final PresignedDeleteObjectRequest presignedDeleteObjectRequest = s3Presigner
                .presignDeleteObject(deleteObjectPresignRequest);
        final String url = presignedDeleteObjectRequest.url().toString();

        s3Presigner.close();
        return url;
    }
}
