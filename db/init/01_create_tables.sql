-- =========================================
-- Agar bisa menggunakan UUID + auto assign
-- =========================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================================
-- Admin table
-- =========================================
CREATE TABLE IF NOT EXISTS admins (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(50) UNIQUE NOT NULL,
    fullname        VARCHAR(100) NOT NULL,
    password_hash   TEXT,

    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

-- =========================================
-- Enum untuk tipe kendaraan
-- =========================================
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'vehicle_type') THEN
        CREATE TYPE vehicle_type AS ENUM ('CAR', 'BIKE');
    END IF;
END$$;

-- =========================================
-- Vehicle table
-- =========================================
CREATE TABLE IF NOT EXISTS vehicles (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                    VARCHAR(100) NOT NULL,
    plate_number            VARCHAR(20),
    daily_rate              NUMERIC(10, 2) NOT NULL,
    type                    vehicle_type NOT NULL,

    -- Atribut khusus Bike
    engine_cc               INTEGER,
    has_top_box             BOOLEAN,

    -- Atribut khusus Car
    seat_count              INTEGER,
    door_count              INTEGER,
    has_auto_transmission   BOOLEAN,

    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ DEFAULT NOW(),
    deleted_at              TIMESTAMPTZ
);

-- =========================================
-- Enum untuk status rental
-- =========================================
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'rental_status') THEN
        CREATE TYPE rental_status AS ENUM ('PENDING', 'RENTING', 'RETURNED', 'REJECTED');
    END IF;
END$$;

-- =========================================
-- Rentals table
-- =========================================
CREATE TABLE IF NOT EXISTS rentals (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id          UUID NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    status              rental_status DEFAULT 'PENDING',

    renter_name         VARCHAR(100) NOT NULL,
    renter_id_number    VARCHAR(50) NOT NULL,
    renter_phone_number VARCHAR(20),
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,

    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ DEFAULT NOW(),
    deleted_at          TIMESTAMPTZ
);

-- =========================================
-- Indexes, agar database lebih cepat
-- =========================================
CREATE INDEX IF NOT EXISTS idx_rentals_vehicle_id ON rentals(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vehicles_type ON vehicles(type);

-- =========================================
-- Trigger untuk `updated_at`
-- =========================================
DO $$
BEGIN
    -- Shared function untuk update kolom updated_at
    IF NOT EXISTS (
        SELECT 1 FROM pg_proc WHERE proname = 'trg_set_timestamp'
    ) THEN
        CREATE OR REPLACE FUNCTION trg_set_timestamp()
        RETURNS TRIGGER AS $func$
        BEGIN
            NEW.updated_at = NOW();
            RETURN NEW;
        END;
        $func$ LANGUAGE plpgsql;
    END IF;

    -- Trigger untuk vehicles
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger WHERE tgname = 'set_vehicles_updated_at'
    ) THEN
        CREATE TRIGGER set_vehicles_updated_at
        BEFORE UPDATE ON vehicles
        FOR EACH ROW
        EXECUTE FUNCTION trg_set_timestamp();
    END IF;

    -- Trigger untuk rentals
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger WHERE tgname = 'set_rentals_updated_at'
    ) THEN
        CREATE TRIGGER set_rentals_updated_at
        BEFORE UPDATE ON rentals
        FOR EACH ROW
        EXECUTE FUNCTION trg_set_timestamp();
    END IF;

    -- Trigger untuk admins
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger WHERE tgname = 'set_admins_updated_at'
    ) THEN
        CREATE TRIGGER set_admins_updated_at
        BEFORE UPDATE ON admins
        FOR EACH ROW
        EXECUTE FUNCTION trg_set_timestamp();
    END IF;
END$$;
