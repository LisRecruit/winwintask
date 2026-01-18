package org.example.processinglog;

import org.example.user.CustomUserDetails;
import org.example.user.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ProcessingLogControllerTest {

    @Test
    void shouldReturnProcessedText() throws Exception {
        UUID userId = UUID.randomUUID();
        String input = "hello";
        String transformed = "OLLEH";

        ProcessingLogService serviceMock = Mockito.mock(ProcessingLogService.class);
        Mockito.when(serviceMock.process(eq(userId), eq(input))).thenReturn(transformed);

        ProcessingLogController controller = new ProcessingLogController(serviceMock);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        User user = new User();
        user.setId(userId);
        user.setEmail("a@a.com");
        user.setPasswordHash("passHash");
        CustomUserDetails principal = new CustomUserDetails(user);

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(principal);

        mockMvc.perform(post("/api/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"hello\"}")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(transformed));
    }
}