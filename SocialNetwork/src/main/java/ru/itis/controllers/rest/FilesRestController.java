package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.services.files.FilesService;
import ru.itis.services.files.ImagesGenerator;



@RestController
@RequestMapping("/app/files")
@RequiredArgsConstructor
public class FilesRestController {
    private final FilesService filesService;
    private final ImagesGenerator imagesGenerator;


    @GetMapping("/{file_name}")
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable("file_name") String fileName) {
        return filesService.getFile(fileName);
    }

    @GetMapping("/random")
    public ResponseEntity<ByteArrayResource> generateImage(@RequestParam("query") String query) {
        return imagesGenerator.generateImage(query);
    }
}
