package com.bsmartone.api.sync.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "application_parameters")
public class ApplicationParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String oauthTokenUrl;
    private String oauthClientId;
    private String oauthClientSecret;
    private String oauthScope;
    private String apiBaseUrl;
    private String apiResourcePath;
    private Integer batchSize;
    private Integer initialOffset;
    private String oracleUploadUrl;
    @Column(columnDefinition = "TEXT")
    private String oracleUploadRequestTemplate;
    private String oracleTargetFilename;
    private Boolean enabled;
    private LocalDateTime updatedAt;
}
