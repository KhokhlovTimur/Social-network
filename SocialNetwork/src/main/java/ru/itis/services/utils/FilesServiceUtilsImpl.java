package ru.itis.services.utils;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class FilesServiceUtilsImpl implements FilesServiceUtils {
    @Override
    public String generateFileName(String originalFileName) {
        return String.format("%s.%s",
                RandomStringUtils.randomAlphanumeric(3) + new Timestamp(System.currentTimeMillis()).getTime() +
                        RandomStringUtils.randomAlphanumeric(3),
                FileNameUtils.getExtension(originalFileName));
    }
}
