package org.example.processinglog;

import org.example.dataapi.DataApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessingLogServiceTest {

    private ProcessingLogRepository repository;
    private DataApiClient dataApiClient;
    private ProcessingLogService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProcessingLogRepository.class);
        dataApiClient = mock(DataApiClient.class);
        service = new ProcessingLogService(repository, dataApiClient);
    }

    @Test
    void shouldProcessAndSaveLog() {
        UUID userId = UUID.randomUUID();
        String input = "hello";
        String transformed = "OLLEH";

        when(dataApiClient.process(input)).thenReturn(transformed);

        String result = service.process(userId, input);

        assertEquals(transformed, result);

        ArgumentCaptor<ProcessingLog> captor =
                ArgumentCaptor.forClass(ProcessingLog.class);

        verify(repository).save(captor.capture());

        ProcessingLog savedLog = captor.getValue();

        assertEquals(userId, savedLog.getUserId());
        assertEquals(input, savedLog.getInputText());
        assertEquals(transformed, savedLog.getOutputText());
        assertNotNull(savedLog.getCreatedAt());
    }

    @Test
    void shouldPropagateException_whenDataApiFails() {
        UUID userId = UUID.randomUUID();
        String input = "hello";

        when(dataApiClient.process(input))
                .thenThrow(new RuntimeException("Data API error"));

        assertThrows(RuntimeException.class,
                () -> service.process(userId, input));

        verify(repository, never()).save(any());
    }
}