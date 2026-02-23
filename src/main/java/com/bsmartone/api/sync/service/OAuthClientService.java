package com.bsmartone.api.sync.service;

import com.bsmartone.api.sync.model.ApplicationParameters;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class OAuthClientService {

    public String getAccessToken(ApplicationParameters parameters) {
        RestClient client = RestClient.create();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", parameters.getOauthClientId());
        body.add("client_secret", parameters.getOauthClientSecret());
        if (parameters.getOauthScope() != null && !parameters.getOauthScope().isBlank()) {
            body.add("scope", parameters.getOauthScope());
        }

        Map<?, ?> response = client.post()
            .uri(parameters.getOauthTokenUrl())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .body(Map.class);

        if (response == null || response.get("access_token") == null) {
            throw new IllegalStateException("OAuth token response missing access_token");
        }
        return String.valueOf(response.get("access_token"));
    }
}
