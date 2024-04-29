package com.kth.journalsystem.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KeycloakTokenExchangeService {
    private static final String TOKEN_EXCHANGE_URL = "http://localhost:8181/realms/Journal/protocol/openid-connect/token";
    private static final String CLIENT_ID = "journal";
    private static final String CLIENT_SECRET = "WgDKnPfDJBm0tqoK1Z1qnP8c9C73PN0I";

    public String exchangeToken(String currentToken, String targetAudience) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        requestBody.add("subject_token", currentToken);
        requestBody.add("subject_token_type", "urn:ietf:params:oauth:token-type:access_token");
        requestBody.add("audience", targetAudience);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(TOKEN_EXCHANGE_URL, HttpMethod.POST,request, Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (response.getStatusCode().is2xxSuccessful() && responseBody != null) {
            return (String) responseBody.get("access_token");
        } else {
            // Handle error response
            return null;
        }
    }
}

