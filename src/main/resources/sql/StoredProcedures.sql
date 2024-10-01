-- upserts a single cve row in the nvd.cve table
CREATE OR REPLACE PROCEDURE nvd.upsert_cve_details(p_cve_id VARCHAR, p_details JSONB)
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
CREATE OR REPLACE PROCEDURE nvd.upsert_metadata(
    p_cves_modified INT,
    p_format VARCHAR,
    p_api_version VARCHAR,
    p_last_timestamp VARCHAR)
LANGUAGE plpgsql
AS $$
BEGIN
  INSERT INTO nvd.metadata (cves_modified, format, api_version, last_timestamp)
    VALUES (p_cves_modified, p_format, p_api_version, p_last_timestamp)
    ON CONFLICT (format) DO UPDATE
        SET cves_modified = EXCLUDED.cves_modified,
            last_timestamp = EXCLUDED.last_timestamp;
END;
$$;

-- upserts cves in batch
CREATE OR REPLACE PROCEDURE nvd.upsert_cve_batch(p_cve_ids TEXT[], p_details JSONB[])
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO nvd.cve (cve_id, details)
    SELECT cve_id, json_build_object('details', detail)
    FROM unnest(p_cve_ids) WITH ORDINALITY AS cve_id(cve_id, ord)
    JOIN unnest(p_details) WITH ORDINALITY AS details(detail, ord)
      ON cve_id.ord = details.ord
    ON CONFLICT (cve_id) DO UPDATE
        SET details = EXCLUDED.details;
END;
$$;

-- deletes rows from a specified table based on specified identifier
CREATE OR REPLACE PROCEDURE nvd.delete_rows(p_ids TEXT[], p_table_name TEXT, p_identifier_name TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
    EXECUTE format('DELETE FROM %I WHERE %I LIKE ANY(p_ids)', p_table_name, p_identifier_name);
END;
$$;