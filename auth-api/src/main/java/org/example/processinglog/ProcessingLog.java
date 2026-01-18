package org.example.processinglog;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processing_log")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessingLog {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private UUID userId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String inputText;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String outputText;
    @Column(nullable = false)
    private Instant createdAt;
}
