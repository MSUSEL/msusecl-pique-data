CREATE SCHEMA IF NOT EXISTS nvd;

CREATE TABLE IF NOT EXISTS nvd.cve (
    id SERIAL PRIMARY KEY,
    cve_id VARCHAR(20) UNIQUE NOT NULL,
    details JSONB
);

CREATE TABLE IF NOT EXISTS nvd.metadata(
    id SERIAL PRIMARY KEY,
    cves_modified INT NOT NULL,
    format VARCHAR(10) UNIQUE NOT NULL,
    api_version VARCHAR(5) UNIQUE NOT NULL,
    last_timestamp VARCHAR(30) UNIQUE NOT NULL
);
