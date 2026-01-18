package org.example.dataapi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${dataapi.url}")
    private String dataApiUrl;

    @Value("${dataapi.internal-token}")
    private String internalToken;

    public String process(String text) {
        Map<String, String> requestBody = Map.of("text", text);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Token", internalToken);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                dataApiUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to get response from Data API: status " + response.getStatusCode());
        }

        Map<String, Object> body = response.getBody();
        if (body == null || body.get("text") == null) {
            throw new RuntimeException("Data API returned null result");
        }

        return body.get("text").toString();
    }
}
