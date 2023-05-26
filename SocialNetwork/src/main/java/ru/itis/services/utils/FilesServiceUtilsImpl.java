package ru.itis.services.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.services.files.FilesService;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class FilesServiceUtilsImpl implements FilesServiceUtils {
    private final FilesService filesService;

    @Override
    public String generatePathToFile(String bucketName, MultipartFile multipartFile, String directories) {
        String imageName = generateFileName(multipartFile.getOriginalFilename());
        return filesService.savePhoto(multipartFile, directories + imageName, bucketName);
    }

    private String generateFileName(String originalFileName) {
        return String.format("%s.%s",
                RandomStringUtils.randomAlphanumeric(3) + new Timestamp(System.currentTimeMillis()).getTime() +
                        RandomStringUtils.randomAlphanumeric(3),
                FileNameUtils.getExtension(originalFileName));
    }
}
