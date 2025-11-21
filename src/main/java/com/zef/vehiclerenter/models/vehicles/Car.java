package com.zef.vehiclerenter.models.vehicles;

import java.util.UUID;

public class Car extends VehicleBase {
    private int seatCount = 2;
    private int doorCount = 2;
    private boolean hasAutoTransmission = false;

    public Car(UUID id, String name, String plateNumber, double dailyRate) {
        super(id, name, plateNumber, dailyRate, VehicleType.CAR);
    }

    public Car(UUID id, String name, String plateNumber, double dailyRate, boolean hasAutoTransmission) {
        this(id, name, plateNumber, dailyRate);
        this.hasAutoTransmission = hasAutoTransmission;
    }

    public Car(UUID id, String name, String plateNumber, double dailyRate, int seatCount, int doorCount, boolean hasAutoTransmission) {
        this(id, name, plateNumber, dailyRate);
        this.seatCount = seatCount;
        this.doorCount = doorCount;
        this.hasAutoTransmission = hasAutoTransmission;
    }
}
