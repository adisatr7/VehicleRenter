CREATE OR REPLACE FUNCTION java_string_hash_hex(s text) RETURNS text AS $$
DECLARE
    h BIGINT := 0;
    i INT;
    c INT;
BEGIN
    IF s IS NULL THEN
        RETURN NULL;
    END IF;
    FOR i IN 1..char_length(s) LOOP
        c := get_byte(convert_to(substr(s,i,1), 'SQL_ASCII'), 0);
        h := (h * 31 + c);
    END LOOP;
    RETURN to_hex((h::bigint & 4294967295));
END;
$$ LANGUAGE plpgsql IMMUTABLE;


INSERT INTO admins (email, fullname, password_hash, created_at)
VALUES (
  'admin@local.dev',
  'Administrator',
  java_string_hash_hex('admin123'),
  now()
)
ON CONFLICT (email) DO NOTHING;
