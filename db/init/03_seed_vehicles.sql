-- =========================================
-- Seed data: kendaraan umum di Indonesia
-- =========================================
DO $$
BEGIN
    -- Motor: Honda Beat
    IF NOT EXISTS (SELECT 1 FROM vehicles WHERE name = 'Honda Beat') THEN
        INSERT INTO vehicles(name, plate_number, daily_rate, type, engine_cc, has_top_box)
        VALUES ('Honda Beat', 'B 1234 XY', 85000.00, 'BIKE', 110, TRUE);
    END IF;

    -- Motor: Yamaha NMAX
    IF NOT EXISTS (SELECT 1 FROM vehicles WHERE name = 'Yamaha NMAX') THEN
        INSERT INTO vehicles(name, plate_number, daily_rate, type, engine_cc, has_top_box)
        VALUES ('Yamaha NMAX', 'B 2345 YZ', 150000.00, 'BIKE', 155, TRUE);
    END IF;

    -- Motor: Yamaha Mio
    IF NOT EXISTS (SELECT 1 FROM vehicles WHERE name = 'Yamaha Mio') THEN
        INSERT INTO vehicles(name, plate_number, daily_rate, type, engine_cc, has_top_box)
        VALUES ('Yamaha Mio', 'B 3456 ZA', 80000.00, 'BIKE', 125, FALSE);
    END IF;

    -- Motor: Suzuki Satria
    IF NOT EXISTS (SELECT 1 FROM vehicles WHERE name = 'Suzuki Satria') THEN
        INSERT INTO vehicles(name, plate_number, daily_rate, type, engine_cc)
        VALUES ('Suzuki Satria', 'B 4567 AB', 120000.00, 'BIKE', 150);
    END IF;

    -- Motor: Honda Vario
    IF NOT EXISTS (SELECT 1 FROM vehicles WHERE name = 'Honda Vario') THEN
        INSERT INTO vehicles(name, plate_number, daily_rate, type, engine_cc, has_top_box)
        VALUES ('Honda Vario', 'B 5678 BC', 95000.00, 'BIKE', 150, TRUE);
    END IF;

    -- Mobil: Toyota Avanza
    IF NOT EXISTS (SELECT 1 FROM vehicles WHERE name = 'Toyota Avanza') THEN
        INSERT INTO vehicles(name, plate_number, daily_rate, type, seat_count, door_count, has_auto_transmission)
        VALUES ('Toyota Avanza', 'B 6789 CD', 400000.00, 'CAR', 7, 5, TRUE);
    END IF;

    -- Mobil: Toyota Innova
    IF NOT EXISTS (SELECT 1 FROM vehicles WHERE name = 'Toyota Innova') THEN
        INSERT INTO vehicles(name, plate_number, daily_rate, type, seat_count, door_count, has_auto_transmission)
        VALUES ('Toyota Innova', 'B 7890 DE', 650000.00, 'CAR', 7, 5, TRUE);
    END IF;

    -- Mobil: Mitsubishi Xpander
    IF NOT EXISTS (SELECT 1 FROM vehicles WHERE name = 'Mitsubishi Xpander') THEN
        INSERT INTO vehicles(name, plate_number, daily_rate, type, seat_count, door_count, has_auto_transmission)
        VALUES ('Mitsubishi Xpander', 'B 8901 EF', 550000.00, 'CAR', 7, 5, TRUE);
    END IF;

    -- Mobil: Honda Brio
    IF NOT EXISTS (SELECT 1 FROM vehicles WHERE name = 'Honda Brio') THEN
        INSERT INTO vehicles(name, plate_number, daily_rate, type, seat_count, door_count, has_auto_transmission)
        VALUES ('Honda Brio', 'B 9012 FG', 300000.00, 'CAR', 5, 5, TRUE);
    END IF;

    -- Mobil: Daihatsu Sigra
    IF NOT EXISTS (SELECT 1 FROM vehicles WHERE name = 'Daihatsu Sigra') THEN
        INSERT INTO vehicles(name, plate_number, daily_rate, type, seat_count, door_count, has_auto_transmission)
        VALUES ('Daihatsu Sigra', 'B 0123 GH', 350000.00, 'CAR', 7, 5, TRUE);
    END IF;
END$$;
