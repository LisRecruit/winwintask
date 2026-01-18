package org.example.processinglog;

import lombok.RequiredArgsConstructor;
import org.example.processinglog.dto.ProcessingLogRequest;
import org.example.processinglog.dto.ProcessingLogResponse;
import org.example.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.attribute.UserPrincipal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/process")
public class ProcessingLogController {
    private final ProcessingLogService processingService;

    @PostMapping
    public ProcessingLogResponse process(
            @RequestBody ProcessingLogRequest request,
            Authentication authentication
    ) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = principal.getUserId();

        String result = processingService.process(userId, request.text());
        return new ProcessingLogResponse(result);
    }
}
