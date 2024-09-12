-- inserts or updates a cve row in the nvd.cve table
CREATE OR REPLACE PROCEDURE upsert_cve_details(p_cve_id VARCHAR, p_details JSONB)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO nvd.cve (cve_id, details)
    VALUES (p_cve_id, p_details)
    ON CONFLICT (cve_id) DO UPDATE
        SET details = EXCLUDED.details;
END;
$$;

-- inserts metadata for initial DB hydration or update.
CREATE OR REPLACE PROCEDURE insert_metadata(
    p_total_cves INT,
    p_cves_modified INT,
    p_format VARCHAR,
    p_api_version VARCHAR,
    p_last_timestamp VARCHAR)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO nvd.metadata (total_cves, cves_modified, format, api_version, last_timestamp)
    VALUES (p_total_cves, p_cves_modified, p_format, p_api_version, p_last_timestamp)
    ON CONFLICT (last_timestamp) DO NOTHING
    IF EXISTS (SELECT 1 FROM nvd.metadata WHERE last_timestamp = p_last_timestamp) THEN
            RAISE EXCEPTION 'Conflict: Metadata with last_timestamp % already exists', p_last_timestamp;
        END IF;
END;
$$;