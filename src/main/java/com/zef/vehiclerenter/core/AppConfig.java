package com.zef.vehiclerenter.core;

public class AppConfig {
    private AppConfig() {}

    public static final String JUDUL_APLIKASI = "Vehicle Renter";

    // Resolusi minimal
    public static final double LEBAR_RESOLUSI_MINIMAL = 1280;
    public static final double TINGGI_RESOLUSI_MINIMAL = 720;

    // -- JANGAN SENTUH SEMUA KONFIGURASI DI BAWAH TULISAN INI --

    // Konfigurasi penting untuk menghubungkan aplikasi ke database
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/vehicle_renter_db";
    public static final String DB_NAME = "vehicle_renter_db";
    public static final String DB_USERNAME = "vehiclerenter";
    public static final String DB_PASSWORD = "secret123";

    public static final int DB_POOL_SIZE = 5;
    public static final long DB_CONNECTION_TIMEOUT = 30_000;    // 30 detik
    public static final long DB_IDLE_TIMEOUT = 600_000;         // 10 menit
    public static final long DB_MAX_LIFETIME = 1_800_000;       // 30 menit
}
