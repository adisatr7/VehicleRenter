package com.zef.vehiclerenter.models.vehicles;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public abstract class VehicleBase {
    protected UUID id;
    protected String name;
    protected String plateNumber;
    protected double dailyRate;
    protected VehicleType type = null;

    public VehicleBase(UUID id, String name, String plateNumber, double dailyRate, VehicleType type) {
        this.id = id;
        this.name = name;
        this.plateNumber = plateNumber;
        this.dailyRate = dailyRate;
        this.type = type;
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
        return days * dailyRate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
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

    public double getDailyRate() {
        return dailyRate;
    }

    public VehicleType getType() {
        return type;
    }
}
