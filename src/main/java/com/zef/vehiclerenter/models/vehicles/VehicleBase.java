package com.zef.vehiclerenter.models.vehicles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public abstract class VehicleBase {
    protected UUID id;
    protected String name;
    protected String plateNumber;
    protected BigDecimal dailyRate;
    protected VehicleType type = null;

    protected OffsetDateTime createdAt = OffsetDateTime.now();
    protected OffsetDateTime updatedAt = OffsetDateTime.now();
    protected OffsetDateTime deletedAt = null;

    public VehicleBase(UUID id, String name, String plateNumber, BigDecimal dailyRate, VehicleType type) {
        this.id = id;
        this.name = name;
        this.plateNumber = plateNumber;
        this.dailyRate = dailyRate;
        this.type = type;
    }

    public VehicleBase(UUID id, String name, String plateNumber, BigDecimal dailyRate, VehicleType type, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this(id, name, plateNumber, dailyRate, type);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public VehicleBase(UUID id, String name, String plateNumber, BigDecimal dailyRate, VehicleType type, OffsetDateTime createdAt, OffsetDateTime updatedAt, OffsetDateTime deletedAt) {
        this(id, name, plateNumber, dailyRate, type, createdAt, updatedAt);
        this.deletedAt = deletedAt;
    }

    /**
     * Hitung biaya sewa berdasarkan tanggal awal dan akhir sewa
     *
     * @param start Tanggal awal sewa
     * @param end Tanggal akhir sewa
     *
     * @return Biaya total sewa untuk rentang waktu tersebut
     */
    public double calculatePrice(LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end);
        if (days <= 0) {
            days = 1;
        }
        return days * dailyRate.doubleValue();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public VehicleType getType() {
        return type;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }
}
