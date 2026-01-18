package org.example.dataapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


class DataApiClientTest {
    private DataApiClient dataApiClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        dataApiClient = new DataApiClient();
        ReflectionTestUtils.setField(dataApiClient, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(dataApiClient, "dataApiUrl",
                "http://localhost:8081/api/transform");
        ReflectionTestUtils.setField(dataApiClient, "internalToken", "testtoken");
    }

    @Test
    void shouldReturnText_whenResponseIsOk() {
        mockServer.expect(requestTo("http://localhost:8081/api/transform"))
                .andExpect(header("X-Internal-Token", "testtoken"))
                .andRespond(withSuccess(
                        "{\"text\":\"OLLEH\"}",
                        MediaType.APPLICATION_JSON
                ));

        String result = dataApiClient.process("hello");

        assertEquals("OLLEH", result);
        mockServer.verify();
    }

    @Test
    void shouldThrowException_whenStatusIsNotOk() {
        mockServer.expect(requestTo("http://localhost:8081/api/transform"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(RuntimeException.class,
                () -> dataApiClient.process("hello"));
    }

    @Test
    void shouldThrowException_whenTextMissing() {
        mockServer.expect(requestTo("http://localhost:8081/api/transform"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        assertThrows(RuntimeException.class,
                () -> dataApiClient.process("hello"));
    }
}