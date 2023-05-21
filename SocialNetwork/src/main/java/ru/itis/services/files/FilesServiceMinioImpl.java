package ru.itis.services.files;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class FilesServiceMinioImpl implements FilesService {
    private final MinioClient minioClient;

    public String savePhoto(MultipartFile file, String myFileName, String bucketName) {
        try (InputStream inputStream = file.getInputStream()) {
            createBucket(bucketName);

            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(myFileName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            minioClient.putObject(args);
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(myFileName)
                            .method(Method.GET)
                            .build());

            return url;
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void createBucket(String bucket) {
        BucketExistsArgs bucketName = BucketExistsArgs.builder()
                .bucket(bucket)
                .build();

        try {
            if (!minioClient.bucketExists(bucketName)) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName.bucket())
                        .build());
            }
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }

    }
}
