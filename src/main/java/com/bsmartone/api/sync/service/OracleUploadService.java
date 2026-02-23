package com.bsmartone.api.sync.service;

import com.bsmartone.api.sync.model.ApplicationParameters;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OracleUploadService {

    public void upload(Path csvPath, ApplicationParameters parameters) {
        RestClient client = RestClient.create();
        String template = parameters.getOracleUploadRequestTemplate();
        if (template != null && !template.isBlank()) {
            String payload = template
                .replace("${fileName}", csvPath.getFileName().toString())
                .replace("${filePath}", csvPath.toAbsolutePath().toString());
            client.post()
                .uri(parameters.getOracleUploadUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
            return;
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("fileName", csvPath.getFileName().toString());
            payload.put("content", Files.readString(csvPath));
            client.post()
                .uri(parameters.getOracleUploadUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
        } catch (Exception e) {
            throw new IllegalStateException("Oracle upload failed", e);
        }
    }
}
