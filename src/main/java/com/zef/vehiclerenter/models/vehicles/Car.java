package com.zef.vehiclerenter.models.vehicles;

import java.math.BigDecimal;
import java.util.UUID;

public class Car extends VehicleBase {
    private int seatCount = 2;
    private int doorCount = 2;
    private boolean hasAutoTransmission = false;

    public Car(UUID id, String name, String plateNumber, BigDecimal dailyRate) {
        super(id, name, plateNumber, dailyRate, VehicleType.CAR);
    }

    public Car(UUID id, String name, String plateNumber, BigDecimal dailyRate, int seatCount, int doorCount, boolean hasAutoTransmission) {
        this(id, name, plateNumber, dailyRate);
        this.seatCount = seatCount;
        this.doorCount = doorCount;
        this.hasAutoTransmission = hasAutoTransmission;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public int getDoorCount() {
        return doorCount;
    }

    public boolean isHasAutoTransmission() {
        return hasAutoTransmission;
    }
}
