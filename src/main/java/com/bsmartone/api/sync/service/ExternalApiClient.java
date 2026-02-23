package com.bsmartone.api.sync.service;

import com.bsmartone.api.sync.dto.BatchResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternalApiClient {

    private final ObjectMapper objectMapper;

    public ExternalApiClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public BatchResponse fetchBatch(String baseUrl, String endpointPath, String bearerToken, int offset, int limit) {
        RestClient client = RestClient.builder().baseUrl(baseUrl).build();
        String raw = client.get()
            .uri(uriBuilder -> uriBuilder
                .path(endpointPath)
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .build())
            .header("Authorization", "Bearer " + bearerToken)
            .retrieve()
            .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(raw == null ? "{}" : raw);
            JsonNode recordsNode = root.path("items");
            if (!recordsNode.isArray()) {
                recordsNode = root.path("data");
            }
            if (!recordsNode.isArray() && root.isArray()) {
                recordsNode = root;
            }
            List<JsonNode> records = new ArrayList<>();
            if (recordsNode.isArray()) {
                recordsNode.forEach(records::add);
            }
            Integer total = root.path("totalCount").isNumber() ? root.path("totalCount").asInt() : null;
            return BatchResponse.builder().records(records).totalCount(total).rawJson(raw).build();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse API response", e);
        }
    }
}
