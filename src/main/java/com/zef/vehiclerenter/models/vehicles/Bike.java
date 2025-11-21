package com.zef.vehiclerenter.models.vehicles;

import java.math.BigDecimal;
import java.util.UUID;

public class Bike extends VehicleBase {
    private int engineCc = 125;
    private boolean hasTopBox = false;

    public Bike(UUID id, String name, String plateNumber, BigDecimal dailyRate) {
        super(id, name, plateNumber, dailyRate, VehicleType.BIKE);
    }

    public Bike(UUID id, String name, String plateNumber, BigDecimal dailyRate, int engineCc, boolean hasTopBox) {
        this(id, name, plateNumber, dailyRate);
        this.engineCc = engineCc;
        this.hasTopBox = hasTopBox;
    }

    public int getEngineCc() {
        return engineCc;
    }

    public boolean isHasTopBox() {
        return hasTopBox;
    }
}
