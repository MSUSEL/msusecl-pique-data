CREATE OR REPLACE PROCEDURE update_cve_details(p_cveId	TEXT, p_details JSONB)
LANGUAGE plpgsql
AS $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM nvd.cve WHERE cveId = p_cveId) THEN
    RAISE EXCEPTION 'CVE with ID % does not exist', p_cveId;
  END IF;

  UPDATE nvd.cve
  SET details = p_details
  WHERE cveId = p_cveId;
END;
$$;


