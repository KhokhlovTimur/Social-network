package ru.itis.services.utils;

import org.springframework.web.multipart.MultipartFile;

public interface FilesServiceUtils {

    String generatePathToFile(String bucketName, MultipartFile multipartFile, String directories);
}
