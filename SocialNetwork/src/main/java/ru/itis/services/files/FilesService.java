package ru.itis.services.files;

import org.springframework.web.multipart.MultipartFile;

public interface FilesService {
    String savePhoto(MultipartFile file, String myFileName, String bucket);
}
