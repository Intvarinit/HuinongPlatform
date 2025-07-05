package com.zhang.huinongplatform.common;

import com.zhang.huinongplatform.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class MinioUtils {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioConfig minioConfig;

    public String uploadFile(MultipartFile file) throws Exception {
        // 检查存储桶是否存在
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioConfig.getBucketName()).build());
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioConfig.getBucketName()).build());
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + "." +
                FilenameUtils.getExtension(file.getOriginalFilename());

        // 上传文件
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        }

//        // 生成7天有效期的签名访问URL,private的也可以访问
//        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
//                .method(Method.GET)
//                .bucket(minioConfig.getBucketName())
//                .object(fileName)
//                .expiry(7, TimeUnit.DAYS)
//                .build());
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + fileName;
    }
}