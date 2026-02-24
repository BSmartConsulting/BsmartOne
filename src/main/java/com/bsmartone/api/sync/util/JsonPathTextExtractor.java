package com.bsmartone.api.sync.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class JsonPathTextExtractor {
    public String extract(JsonNode source, String path, String defaultValue) {
        if (source == null || path == null || path.isBlank()) {
            return defaultValueOrEmpty(defaultValue);
        }
        JsonNode current = source;
        for (String part : path.split("\\.")) {
            if (current == null || current.isMissingNode() || current.isNull()) {
                return defaultValueOrEmpty(defaultValue);
            }
            current = current.path(part);
        }
        if (current == null || current.isMissingNode() || current.isNull()) {
            return defaultValueOrEmpty(defaultValue);
        }
        return current.asText("");
    }

    private String defaultValueOrEmpty(String defaultValue) {
        return defaultValue == null ? "" : defaultValue;
    }
}
