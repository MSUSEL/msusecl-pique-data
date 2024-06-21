CREATE TABLE if NOT EXISTS nvd_cve (
    id SERIAL PRIMARY KEY,
    cve jsonb
)