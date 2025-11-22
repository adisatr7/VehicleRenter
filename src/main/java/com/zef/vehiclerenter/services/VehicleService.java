package com.zef.vehiclerenter.services;

import com.zef.vehiclerenter.jooq.enums.RentalStatus;
import com.zef.vehiclerenter.jooq.tables.records.VehiclesRecord;
import com.zef.vehiclerenter.models.vehicles.Bike;
import com.zef.vehiclerenter.models.vehicles.Car;
import com.zef.vehiclerenter.models.vehicles.VehicleBase;
import com.zef.vehiclerenter.models.vehicles.VehicleType;
import org.jooq.DSLContext;
import static com.zef.vehiclerenter.jooq.tables.Vehicles.VEHICLES;
import static com.zef.vehiclerenter.jooq.tables.Rentals.RENTALS;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VehicleService {
    private final DSLContext db;

    public VehicleService(DSLContext db) {
        this.db = db;
    }

    /**
     * Lihat semua data kendaraan, dengan urutan:
     * 1) Kendaraan yang available (tidak sedang RENTING) muncul duluan
     * 2) Di dalam masing-masing grup, diurutkan berdasarkan abjad (A-Z)
     *
     * @return Daftar semua data kendaraan yang ada di database
     */
    public List<VehicleBase> getAll() {
        // Ambil semua kendaraan yang belum dihapus
        var vehicleRecords = db.selectFrom(VEHICLES)
                .where(VEHICLES.DELETED_AT.isNull())
                .fetch();

        // Ambil semua `vehicle_id` yang sedang sedang disewa (unavailable)
        var unavailableVehicleIds = db.select(RENTALS.VEHICLE_ID)
                .from(RENTALS)
                .where(RENTALS.DELETED_AT.isNull()
                .and(RENTALS.STATUS.in(RentalStatus.RENTING, RentalStatus.PENDING)))
                .fetchSet(RENTALS.VEHICLE_ID);

        return vehicleRecords.stream()
                .map(this::toDomain)
                .sorted((v1, v2) -> {
                    // Urutkan agar kendaraan yang available ditampilkan duluan, sedangkan kendaraan yang
                    // sedang disewa ditampilkan belakangan
                    boolean v1Available = !unavailableVehicleIds.contains(v1.getId());
                    boolean v2Available = !unavailableVehicleIds.contains(v2.getId());

                    if (v1Available != v2Available) {
                        return Boolean.compare(!v1Available, !v2Available);
                    }

                    return v1.getName().compareToIgnoreCase(v2.getName());
                })
                .collect(Collectors.toList());
    }

    /**
     * Cari data kendaraan berdasarkan ID-nya yang terdaftar di database
     *
     * @param id ID kendaraan yang ingin dilihat datanya
     *
     * @return Data kendaraan
     */
    public VehicleBase getById(UUID id) {
        VehiclesRecord record = db.selectFrom(VEHICLES)
                .where(VEHICLES.ID.eq(id)
                .and(VEHICLES.DELETED_AT.isNull())) // Jangan tampilkan data yang sudah dihapus
                .fetchOne();

        if (record == null) {
            return null;
        }
        return toDomain(record);
    }

    /**
     * Tambah atau edit data kendaraan
     *
     * @param vehicle Data kendaraan yang akan ditambah/edit
     */
    public void save(VehicleBase vehicle) {
        if (vehicle.getId() == null) {
            insert(vehicle); // Jika data belum ada, tambahkan data ke database
        } else {
            update(vehicle); // Jika data sudah ada, lakukan edit data saja
        }
    }

    /**
     * Hapus kendaraan dari database
     *
     * @param id
     */
    public void delete(UUID id) {
        db.update(VEHICLES)
                .set(VEHICLES.DELETED_AT, OffsetDateTime.now())
                .where(VEHICLES.ID.eq(id)
                        .and(VEHICLES.DELETED_AT.isNull()))
                .execute();
    }

    /**
     * Konversi data polos yang diterima dari database agar bisa digunakan di aplikasi
     * sesuai deklarasi yang ada di package `models
     *`
     * @param record Data raw dari database
     *
     * @return Data yang sudah disesuaikan untuk dipakai oleh aplikasi
     */
    private VehicleBase toDomain(VehiclesRecord record) {
        VehicleType type = VehicleType.valueOf(record.getType().name());

        return switch (type) {
            // Untuk jenis kendaraan motor, gunakan class Bike
            case BIKE -> new Bike(
                    record.getId(),
                    record.getName(),
                    record.getPlateNumber(),
                    record.getDailyRate(),
                    record.getEngineCc() != null ? record.getEngineCc() : 150,
                    record.getHasTopBox() != null && record.getHasTopBox()
            );

            // Untuk jenis kendaraan mobil, gunakan class Car
            case CAR -> new Car(
                    record.getId(),
                    record.getName(),
                    record.getPlateNumber(),
                    record.getDailyRate(),
                    record.getSeatCount() != null ? record.getSeatCount() : 2,
                    record.getDoorCount() != null ? record.getDoorCount() : 2,
                    record.getHasAutoTransmission() != null && record.getHasAutoTransmission()
            );

            // Jika selain itu, lempar error. Aplikasi ini hanya mendukung Bike (motor) dan Car (mobil) saja
            default -> throw new IllegalArgumentException("Unknown vehicle type: " + record.getType());
        };
    }

    /**
     * Fungsi internal untuk memasukkan data baru ke tabel `vehicles` di database
     *
     * @param vehicle Data kendaraan yang akan dimasukkan
     */
    private void insert(VehicleBase vehicle) {
        if (vehicle instanceof Car) {
            Car car = (Car) vehicle;
            db.insertInto(VEHICLES)
                    .set(VEHICLES.NAME, car.getName())
                    .set(VEHICLES.PLATE_NUMBER, car.getPlateNumber())
                    .set(VEHICLES.DAILY_RATE, car.getDailyRate())
                    .set(VEHICLES.TYPE, com.zef.vehiclerenter.jooq.enums.VehicleType.CAR)
                    .set(VEHICLES.SEAT_COUNT, car.getSeatCount())
                    .set(VEHICLES.DOOR_COUNT, car.getDoorCount())
                    .set(VEHICLES.HAS_AUTO_TRANSMISSION, car.isHasAutoTransmission())
                    .execute();
        } else if (vehicle instanceof Bike) {
            Bike bike = (Bike) vehicle;
            db.insertInto(VEHICLES)
                    .set(VEHICLES.NAME, bike.getName())
                    .set(VEHICLES.PLATE_NUMBER, bike.getPlateNumber())
                    .set(VEHICLES.DAILY_RATE, bike.getDailyRate())
                    .set(VEHICLES.TYPE, com.zef.vehiclerenter.jooq.enums.VehicleType.BIKE)
                    .set(VEHICLES.ENGINE_CC, bike.getEngineCc())
                    .set(VEHICLES.HAS_TOP_BOX, bike.isHasTopBox())
                    .execute();
        }
    }

    /**
     * Fungsi internal untuk mengubah data di tabel `vehicles` di database
     *
     * @param vehicle Data baru yang ingin dimasukkan nilainya
     */
    private void update(VehicleBase vehicle) {
        if (vehicle instanceof Car) {
            Car car = (Car) vehicle;
            db.update(VEHICLES)
                    .set(VEHICLES.NAME, car.getName())
                    .set(VEHICLES.PLATE_NUMBER, car.getPlateNumber())
                    .set(VEHICLES.DAILY_RATE, car.getDailyRate())
                    .set(VEHICLES.SEAT_COUNT, car.getSeatCount())
                    .set(VEHICLES.DOOR_COUNT, car.getDoorCount())
                    .set(VEHICLES.HAS_AUTO_TRANSMISSION, car.isHasAutoTransmission())
                    .where(VEHICLES.ID.eq(car.getId()))
                    .execute();
        } else if (vehicle instanceof Bike) {
            Bike bike = (Bike) vehicle;
            db.update(VEHICLES)
                    .set(VEHICLES.NAME, bike.getName())
                    .set(VEHICLES.PLATE_NUMBER, bike.getPlateNumber())
                    .set(VEHICLES.DAILY_RATE, bike.getDailyRate())
                    .set(VEHICLES.ENGINE_CC, bike.getEngineCc())
                    .set(VEHICLES.HAS_TOP_BOX, bike.isHasTopBox())
                    .where(VEHICLES.ID.eq(bike.getId()))
                    .execute();
        }
    }
}
