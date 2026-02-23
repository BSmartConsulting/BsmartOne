CREATE TABLE IF NOT EXISTS application_parameters (
    id BIGSERIAL PRIMARY KEY,
    oauth_token_url TEXT NOT NULL,
    oauth_client_id TEXT NOT NULL,
    oauth_client_secret TEXT NOT NULL,
    oauth_scope TEXT,
    api_base_url TEXT NOT NULL,
    api_resource_path TEXT,
    batch_size INTEGER NOT NULL DEFAULT 200,
    initial_offset INTEGER NOT NULL DEFAULT 0,
    oracle_upload_url TEXT NOT NULL,
    oracle_upload_request_template TEXT,
    oracle_target_filename TEXT,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS entity_configuration (
    id BIGSERIAL PRIMARY KEY,
    entity_name TEXT NOT NULL UNIQUE,
    endpoint_path TEXT NOT NULL,
    country_field TEXT,
    file_name TEXT,
    delimiter TEXT NOT NULL DEFAULT ',',
    line_separator TEXT NOT NULL DEFAULT E'\\n',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT
);

CREATE TABLE IF NOT EXISTS entity_field_mapping (
    id BIGSERIAL PRIMARY KEY,
    entity_configuration_id BIGINT NOT NULL REFERENCES entity_configuration(id),
    field_order INTEGER NOT NULL,
    field_name TEXT NOT NULL,
    json_path TEXT NOT NULL,
    default_value TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_entity_field_mapping_order
    ON entity_field_mapping(entity_configuration_id, field_order);

CREATE TABLE IF NOT EXISTS raw_batch (
    id BIGSERIAL PRIMARY KEY,
    entity_name TEXT NOT NULL,
    country TEXT,
    source_offset INTEGER NOT NULL,
    batch_size INTEGER NOT NULL,
    records_count INTEGER NOT NULL,
    raw_json TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS raw_record (
    id BIGSERIAL PRIMARY KEY,
    entity_name TEXT NOT NULL,
    country TEXT,
    source_offset INTEGER NOT NULL,
    source_index INTEGER NOT NULL,
    record_text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_raw_record_entity_id ON raw_record(entity_name, id);
CREATE INDEX IF NOT EXISTS idx_raw_record_entity_country ON raw_record(entity_name, country);

CREATE TABLE IF NOT EXISTS sync_run (
    id BIGSERIAL PRIMARY KEY,
    entity_name TEXT NOT NULL,
    status TEXT NOT NULL,
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    total_expected INTEGER,
    processed_count INTEGER NOT NULL DEFAULT 0,
    current_offset INTEGER NOT NULL DEFAULT 0,
    progress_percent NUMERIC(5,2) NOT NULL DEFAULT 0,
    csv_path TEXT,
    oracle_upload_status TEXT,
    error_message TEXT
);
