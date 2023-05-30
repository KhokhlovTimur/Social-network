package ru.itis.services.utils;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FilesServiceUtils {

    String generatePathToFile(MultipartFile multipartFile);
}
