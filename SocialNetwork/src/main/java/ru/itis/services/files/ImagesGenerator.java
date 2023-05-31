package ru.itis.services.files;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

public interface ImagesGenerator {
    ResponseEntity<ByteArrayResource> generateImage(String query);
}
