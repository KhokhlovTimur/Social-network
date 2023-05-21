package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.services.files.FilesService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FilesContr {
    private final FilesService photoService;

    @PostMapping("/photos")
    public String uploadPhoto(@RequestParam("file") MultipartFile file) throws IOException {
        // Сохраняем фото
        String photoUrl = photoService.savePhoto(file, "/wef/1233.txt", "123");

        // Возвращаем URL сохраненного фото в ответе
        return photoUrl;
    }
}
