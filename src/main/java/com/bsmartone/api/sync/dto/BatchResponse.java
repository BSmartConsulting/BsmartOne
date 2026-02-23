package com.bsmartone.api.sync.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BatchResponse {
    List<JsonNode> records;
    Integer totalCount;
    String rawJson;
}
