package com.bsmartone.api.sync.service;

import com.bsmartone.api.sync.dto.BatchResponse;
import com.bsmartone.api.sync.model.*;
import com.bsmartone.api.sync.repository.*;
import com.bsmartone.api.sync.util.CsvTextEscaper;
import com.bsmartone.api.sync.util.JsonPathTextExtractor;
import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SyncOrchestratorService {
    private static final Logger log = LoggerFactory.getLogger(SyncOrchestratorService.class);

    private final ApplicationParametersRepository appParamsRepository;
    private final EntityConfigurationRepository entityConfigurationRepository;
    private final EntityFieldMappingRepository fieldMappingRepository;
    private final RawBatchRepository rawBatchRepository;
    private final RawRecordRepository rawRecordRepository;
    private final SyncRunRepository syncRunRepository;
    private final OAuthClientService oAuthClientService;
    private final ExternalApiClient externalApiClient;
    private final OracleUploadService oracleUploadService;
    private final JsonPathTextExtractor jsonPathTextExtractor;
    private final CsvTextEscaper csvTextEscaper;

    public SyncOrchestratorService(ApplicationParametersRepository appParamsRepository,
                                   EntityConfigurationRepository entityConfigurationRepository,
                                   EntityFieldMappingRepository fieldMappingRepository,
                                   RawBatchRepository rawBatchRepository,
                                   RawRecordRepository rawRecordRepository,
                                   SyncRunRepository syncRunRepository,
                                   OAuthClientService oAuthClientService,
                                   ExternalApiClient externalApiClient,
                                   OracleUploadService oracleUploadService,
                                   JsonPathTextExtractor jsonPathTextExtractor,
                                   CsvTextEscaper csvTextEscaper) {
        this.appParamsRepository = appParamsRepository;
        this.entityConfigurationRepository = entityConfigurationRepository;
        this.fieldMappingRepository = fieldMappingRepository;
        this.rawBatchRepository = rawBatchRepository;
        this.rawRecordRepository = rawRecordRepository;
        this.syncRunRepository = syncRunRepository;
        this.oAuthClientService = oAuthClientService;
        this.externalApiClient = externalApiClient;
        this.oracleUploadService = oracleUploadService;
        this.jsonPathTextExtractor = jsonPathTextExtractor;
        this.csvTextEscaper = csvTextEscaper;
    }

    @Transactional
    public SyncRun createRun(String entityName) {
        SyncRun run = new SyncRun();
        run.setEntityName(entityName);
        run.setStatus(SyncRunStatus.PENDING);
        run.setProgressPercent(BigDecimal.ZERO);
        run.setProcessedCount(0);
        run.setCurrentOffset(0);
        return syncRunRepository.save(run);
    }

    @Async
    public void executeRun(Long runId) {
        SyncRun run = syncRunRepository.findById(runId).orElseThrow();
        try {
            run.setStatus(SyncRunStatus.RUNNING);
            run.setStartedAt(LocalDateTime.now());
            syncRunRepository.save(run);

            ApplicationParameters params = appParamsRepository.findFirstByEnabledTrue()
                .orElseThrow(() -> new IllegalStateException("No active application parameters found"));
            EntityConfiguration entity = entityConfigurationRepository
                .findByEntityNameAndEnabledTrue(run.getEntityName())
                .orElseThrow(() -> new IllegalStateException("Entity config not found: " + run.getEntityName()));
            List<EntityFieldMapping> mappings = fieldMappingRepository
                .findByEntityConfigurationAndEnabledTrueOrderByFieldOrderAsc(entity);

            String token = oAuthClientService.getAccessToken(params);
            int offset = params.getInitialOffset() == null ? 0 : params.getInitialOffset();
            int batchSize = params.getBatchSize() == null ? 200 : params.getBatchSize();
            int processed = 0;
            Integer knownTotal = null;

            while (true) {
                long start = System.currentTimeMillis();
                BatchResponse response = externalApiClient.fetchBatch(
                    params.getApiBaseUrl(), entity.getEndpointPath(), token, offset, batchSize);

                int count = response.getRecords().size();
                if (knownTotal == null && response.getTotalCount() != null) {
                    knownTotal = response.getTotalCount();
                }
                if (count == 0) {
                    break;
                }

                saveRawBatch(run.getEntityName(), offset, batchSize, count, response.getRawJson());
                saveRawRecords(run.getEntityName(), entity, mappings, response.getRecords(), offset);

                processed += count;
                offset += batchSize;
                updateProgress(run, processed, offset, knownTotal);
                log.info("Batch processed entity={} offset={} count={} elapsedMs={}", run.getEntityName(), offset, count,
                    System.currentTimeMillis() - start);

                if (count < batchSize) {
                    break;
                }
            }

            Path csv = buildCsv(run.getEntityName(), entity, params);
            run.setCsvPath(csv.toString());
            run.setProgressPercent(new BigDecimal("100.00"));
            run.setOracleUploadStatus("UPLOADING");
            syncRunRepository.save(run);
            oracleUploadService.upload(csv, params);

            run.setStatus(SyncRunStatus.COMPLETED);
            run.setOracleUploadStatus("SUCCESS");
            run.setEndedAt(LocalDateTime.now());
            syncRunRepository.save(run);
        } catch (Exception e) {
            run.setStatus(SyncRunStatus.FAILED);
            run.setOracleUploadStatus("FAILED");
            run.setErrorMessage(e.getMessage());
            run.setEndedAt(LocalDateTime.now());
            syncRunRepository.save(run);
            log.error("Sync failed runId={}", runId, e);
        }
    }

    private void saveRawBatch(String entityName, int offset, int batchSize, int count, String rawJson) {
        RawBatch batch = new RawBatch();
        batch.setEntityName(entityName);
        batch.setSourceOffset(offset);
        batch.setBatchSize(batchSize);
        batch.setRecordsCount(count);
        batch.setRawJson(rawJson == null ? "[]" : rawJson);
        batch.setCreatedAt(LocalDateTime.now());
        rawBatchRepository.save(batch);
    }

    private void saveRawRecords(String entityName,
                                EntityConfiguration config,
                                List<EntityFieldMapping> mappings,
                                List<JsonNode> records,
                                int offset) {
        List<RawRecord> toSave = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            JsonNode record = records.get(i);
            String line = mappings.stream()
                .map(mapping -> jsonPathTextExtractor.extract(record, mapping.getJsonPath(), mapping.getDefaultValue()))
                .map(value -> csvTextEscaper.escape(value, config.getDelimiter()))
                .collect(Collectors.joining(config.getDelimiter()));
            RawRecord rawRecord = new RawRecord();
            rawRecord.setEntityName(entityName);
            rawRecord.setSourceOffset(offset);
            rawRecord.setSourceIndex(i);
            rawRecord.setRecordText(line);
            rawRecord.setCreatedAt(LocalDateTime.now());
            if (config.getCountryField() != null && !config.getCountryField().isBlank()) {
                rawRecord.setCountry(jsonPathTextExtractor.extract(record, config.getCountryField(), ""));
            }
            toSave.add(rawRecord);
        }
        rawRecordRepository.saveAll(toSave);
    }

    private void updateProgress(SyncRun run, int processed, int offset, Integer totalExpected) {
        run.setProcessedCount(processed);
        run.setCurrentOffset(offset);
        run.setTotalExpected(totalExpected);
        if (totalExpected != null && totalExpected > 0) {
            BigDecimal progress = BigDecimal.valueOf(processed)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalExpected), 2, RoundingMode.HALF_UP);
            run.setProgressPercent(progress.min(new BigDecimal("100.00")));
        }
        syncRunRepository.save(run);
    }

    private Path buildCsv(String entityName, EntityConfiguration config, ApplicationParameters params) {
        try {
            String filename = params.getOracleTargetFilename() != null && !params.getOracleTargetFilename().isBlank()
                ? params.getOracleTargetFilename()
                : (config.getFileName() == null ? entityName + ".csv" : config.getFileName());
            Path tmp = Files.createTempFile(filename.replace(".csv", ""), ".csv");
            int page = 0;
            int size = 1000;
            while (true) {
                List<RawRecord> rows = rawRecordRepository.findByEntityNameOrderByIdAsc(entityName, PageRequest.of(page, size));
                if (rows.isEmpty()) {
                    break;
                }
                List<String> lines = rows.stream()
                    .map(RawRecord::getRecordText)
                    .map(line -> line + config.getLineSeparator())
                    .toList();
                Files.write(tmp, lines, java.nio.file.StandardOpenOption.APPEND);
                page++;
            }
            return tmp;
        } catch (Exception e) {
            throw new IllegalStateException("CSV generation failed", e);
        }
    }
}
