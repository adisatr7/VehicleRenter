package com.zef.vehiclerenter.models;

import com.zef.vehiclerenter.models.vehicles.VehicleBase;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Rental {
    private final UUID id;
    private final VehicleBase vehicle;
    private RentalStatus status;

    private final String renterName;
    private final String renterIdNumber;
    private final String renterPhoneNumber;

    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    private OffsetDateTime deletedAt = null;

    public Rental(
            UUID id,
            VehicleBase vehicle,
            String renterName,
            String renterIdNumber,
            String renterPhoneNumber
    ) {
        this.id = id;
        this.vehicle = vehicle;
        this.renterName = renterName;
        this.renterIdNumber = renterIdNumber;
        this.renterPhoneNumber = renterPhoneNumber;
        this.status = RentalStatus.PENDING;
    }

    public Rental(
            UUID id,
            VehicleBase vehicle,
            String renterName,
            String renterIdNumber,
            String renterPhoneNumber,
            RentalStatus status
    ) {
        this(id, vehicle, renterName, renterIdNumber, renterPhoneNumber);
        this.status = status;
    }

    public Rental(
            UUID id,
            VehicleBase vehicle,
            String renterName,
            String renterIdNumber,
            String renterPhoneNumber,
            RentalStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this(id, vehicle, renterName, renterIdNumber, renterPhoneNumber, status);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Rental(
            UUID id,
            VehicleBase vehicle,
            String renterName,
            String renterIdNumber,
            String renterPhoneNumber,
            RentalStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            OffsetDateTime deletedAt
    ) {
        this(id, vehicle, renterName, renterIdNumber, renterPhoneNumber, status, createdAt, updatedAt);
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return id;
    }

    public VehicleBase getVehicle() {
        return vehicle;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public String getRenterName() {
        return renterName;
    }

    public String getRenterIdNumber() {
        return renterIdNumber;
    }

    public String getRenterPhoneNumber() {
        return renterPhoneNumber;
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
