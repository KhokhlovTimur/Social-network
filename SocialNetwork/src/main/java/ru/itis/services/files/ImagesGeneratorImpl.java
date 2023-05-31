package ru.itis.services.files;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ImagesGeneratorImpl implements ImagesGenerator {
    private final static String API_URL = "https://api.api-ninjas.com/v1/randomimage?category=";
    private final static String KEY_HEADER = "X-Api-Key";
    private final static String API_KEY = "etU1s6FX5E8MmDLMnHrkxA==enUPMMoP7hB7pSLY";

    @Override
    public ResponseEntity<ByteArrayResource> generateImage(String query) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL + query + "&width=1920&height=1080")
                .header(KEY_HEADER, API_KEY)
                .header("Accept", "image/jpg")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                byte[] photoBytes = response.body().bytes();
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                        .body(new ByteArrayResource(photoBytes));
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
