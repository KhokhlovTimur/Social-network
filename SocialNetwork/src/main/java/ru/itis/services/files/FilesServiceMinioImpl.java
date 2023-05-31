package ru.itis.services.files;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @Value("${minio.bucketName}")
    private String bucketName;

    @Override
    public ResponseEntity<ByteArrayResource> getFile(String fileName) {
        try (InputStream inputStream = minioClient.getObject(GetObjectArgs
                .builder()
                .bucket(bucketName)
                .object(fileName)
                .build())) {

            byte[] serializeFile = IOUtils.toByteArray(inputStream);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).contentLength(serializeFile.length)
                    .body(new ByteArrayResource(serializeFile));
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException | InternalException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

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

            return "/app/files/" + myFileName;
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
