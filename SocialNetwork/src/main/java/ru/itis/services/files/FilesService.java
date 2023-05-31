package ru.itis.services.files;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FilesService {
    String savePhoto(MultipartFile file, String myFileName, String bucket);

    ResponseEntity<ByteArrayResource> getFile(String fileName);

}
