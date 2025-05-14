package com.streamit.application.services.minio;

import com.streamit.application.utils.FileUtil;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String uploadFile(MultipartFile file, String fileName) throws MinioException {
        String objectName = FileUtil.generateFileName(fileName);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return objectName;
        } catch (Exception e) {
            throw new MinioException("Failed to upload file: " + e.getMessage());
        }
    }

//    public String getFileUrl(String objectName) throws MinioException {
//        try {
//            return minioClient.getPresignedObjectUrl(
//                    GetPresignedObjectUrlArgs.builder()
//                            .method(Method.GET)
//                            .bucket(bucketName)
//                            .object(objectName)
//                            .expiry(7, TimeUnit.DAYS)
//                            .build());
//        } catch (Exception e) {
//            throw new MinioException("Failed to generate URL: " + e.getMessage());
//        }
//    }

    public void deleteFile(String objectName) throws MinioException {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new MinioException("Failed to delete file: " + e.getMessage());
        }
    }

    private boolean fileExists(String objectName) throws MinioException {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw new MinioException("Error checking file existence: " + e.getMessage());
        } catch (Exception e) {
            throw new MinioException("Error checking file existence: " + e.getMessage());
        }
    }

    public InputStream getFileAsStream(String objectName)
            throws MinioException {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new MinioException(e.getMessage());
        }
    }

    public String getContentType(String objectName) throws MinioException {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return stat.contentType() != null ? stat.contentType() : "application/octet-stream";
        } catch (Exception e) {
            throw new MinioException(e.getMessage());
        }
    }
}
