CREATE DATABASE nvd_mirror;

CREATE SCHEMA IF NOT EXISTS nvd;

CREATE TABLE IF NOT EXISTS nvd.cve (
    id SERIAL PRIMARY KEY,
    cve_id VARCHAR(20) UNIQUE NOT NULL,
    details JSONB
);

CREATE TABLE IF NOT EXISTS nvd.metadata(
    id SERIAL PRIMARY KEY,
    total_cves INT NOT NULL,
    cves_modified INT NOT NULL,
    format VARCHAR(10) NOT NULL,
    api_version VARCHAR(5) NOT NULL,
    last_timestamp VARCHAR(30) UNIQUE NOT NULL
);

