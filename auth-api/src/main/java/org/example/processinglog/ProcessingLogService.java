package org.example.processinglog;

import lombok.RequiredArgsConstructor;
import org.example.dataapi.DataApiClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessingLogService {

    private final ProcessingLogRepository repository;
    private final DataApiClient dataApiClient;

    @Transactional
    public String process(UUID userId, String input) {

        String result = dataApiClient.process(input);

        ProcessingLog log = ProcessingLog.builder()
                .userId(userId)
                .inputText(input)
                .outputText(result)
                .createdAt(Instant.now())
                .build();

        repository.save(log);

        return result;
    }
}
