package com.bsmartone.api.sync.util;

import org.springframework.stereotype.Component;

@Component
public class CsvTextEscaper {
    public String escape(String value, String delimiter) {
        String safeValue = value == null ? "" : value;
        boolean shouldQuote = safeValue.contains("\"")
            || safeValue.contains("\n")
            || safeValue.contains("\r")
            || safeValue.contains(delimiter);

        if (!shouldQuote) {
            return safeValue;
        }
        return '"' + safeValue.replace("\"", "\"\"") + '"';
    }
}
