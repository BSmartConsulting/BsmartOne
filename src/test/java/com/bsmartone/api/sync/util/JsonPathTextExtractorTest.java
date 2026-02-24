package com.bsmartone.api.sync.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonPathTextExtractorTest {

    private final JsonPathTextExtractor extractor = new JsonPathTextExtractor();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void extractNestedPathAsText() throws Exception {
        String json = "{\"provider\":{\"pointId\":123},\"missing\":null}";
        assertEquals("123", extractor.extract(mapper.readTree(json), "provider.pointId", ""));
        assertEquals("N/A", extractor.extract(mapper.readTree(json), "provider.country", "N/A"));
    }
}
