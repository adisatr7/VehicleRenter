package com.zef.vehiclerenter.services;

import com.zef.vehiclerenter.jooq.tables.records.RentalsRecord;
import com.zef.vehiclerenter.jooq.tables.records.VehiclesRecord;
import com.zef.vehiclerenter.models.Rental;
import com.zef.vehiclerenter.models.RentalStatus;
import com.zef.vehiclerenter.models.vehicles.VehicleBase;
import com.zef.vehiclerenter.models.vehicles.Car;
import com.zef.vehiclerenter.models.vehicles.Bike;
import com.zef.vehiclerenter.models.vehicles.VehicleType;
import org.jooq.DSLContext;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.zef.vehiclerenter.jooq.tables.Rentals.RENTALS;
import static com.zef.vehiclerenter.jooq.tables.Vehicles.VEHICLES;

public class RentalService {
    private final DSLContext db;
    private final VehicleService vehicleService;

    public RentalService(DSLContext db, VehicleService vehicleService) {
        this.db = db;
        this.vehicleService = vehicleService;
    }

    /**
     * Ambil semua catatan rental
     *
     * @return Data rentalan
     */
    public List<Rental> getAll() {
        return db.selectFrom(RENTALS)
                .where(RENTALS.DELETED_AT.isNull()) // Jangan tampilkan data yang sudah dihapus
                .orderBy(RENTALS.CREATED_AT.desc())
                .fetch()
                .stream()
                .map(this::toDomainWithVehicleLookup)
                .collect(Collectors.toList());
    }

    /**
     * Cari satu data rentalan berdasarkan ID
     *
     * @param id ID data rentalan yang ingin dicari
     *
     * @return Data rentalan yang dicari
     */
    public Rental getById(UUID id) {
        RentalsRecord record = db.selectFrom(RENTALS)
                .where(RENTALS.ID.eq(id)
                .and(RENTALS.DELETED_AT.isNull())) // Jangan tampilkan data yang sudah dihapus
                .fetchOne();

        if (record == null) {
            return null;
        }
        return toDomainWithVehicleLookup(record);
    }

    /**
     * Buat data rental baru
     *
     * @param vehicleId ID kendaraan yang dirental
     * @param renterName Penyewa kendaraan
     * @param renterIdNumber NIK penyewa kendaraan
     * @param renterPhoneNumber No HP penyewa kendaraan
     * @param startDate Tanggal mulai sewa
     * @param endDate Tanggal selesai sewa
     *
     * @return Data rentalan yang baru dibuat
     */
    public Rental createRental(
            UUID vehicleId,
            String renterName,
            String renterIdNumber,
            String renterPhoneNumber,
            LocalDate startDate,
            LocalDate endDate
    ) {
        // Pastikan kendaraan yang ingin disewa benar-benar ada di database
        VehicleBase vehicle = vehicleService.getById(vehicleId);
        if (vehicle == null) {
            // Jika tidak ada, lempar error
            throw new IllegalArgumentException("Vehicle not found for id: " + vehicleId);
        }

        // Simpan data rentalan baru ke database
        RentalsRecord record = db.insertInto(RENTALS)
                .set(RENTALS.VEHICLE_ID, vehicleId)
                .set(RENTALS.STATUS, toDbStatus(RentalStatus.PENDING))
                .set(RENTALS.RENTER_NAME, renterName)
                .set(RENTALS.RENTER_ID_NUMBER, renterIdNumber)
                .set(RENTALS.RENTER_PHONE_NUMBER, renterPhoneNumber)
                .set(RENTALS.START_DATE, startDate)
                .set(RENTALS.END_DATE, endDate)
                .returning()
                .fetchOne();

        // Jika proses penyimpanan gagal, lempar error
        if (record == null) {
            throw new IllegalStateException("Failed to create rental");
        }

        return toDomain(record, vehicle);
    }

    /**
     * Ubah status data rentalan yang sudah ada
     *
     * @param rentalId ID data rentalan yang ingin diubah
     * @param newStatus Status baru
     */
    public void updateStatus(UUID rentalId, RentalStatus newStatus) {
        db.update(RENTALS)
                .set(RENTALS.STATUS, toDbStatus(newStatus))
                .set(RENTALS.UPDATED_AT, OffsetDateTime.now())
                .where(RENTALS.ID.eq(rentalId)
                        .and(RENTALS.DELETED_AT.isNull()))
                .execute();
    }

    /**
     * Hapus data rentalan
     *
     * @param rentalId ID rentalan yang ingin dihapus
     */
    public void delete(UUID rentalId) {
        db.update(RENTALS)
                .set(RENTALS.DELETED_AT, OffsetDateTime.now())
                .where(RENTALS.ID.eq(rentalId)
                .and(RENTALS.DELETED_AT.isNull())) // Jangan hapus data yang memang sudah dihapus sebelumnya
                .execute();
    }

    /**
     * Ambil semua riwayat penyewaan berdasarkan ID kendaraan
     *
     * @param vehicleId ID kendaraan yang ingin dilihat riwayatnya
     *
     * @return Riwayat penyewaan
     */
    public List<Rental> listByVehicle(UUID vehicleId) {
        return db.selectFrom(RENTALS)
                .where(RENTALS.VEHICLE_ID.eq(vehicleId)
                        .and(RENTALS.DELETED_AT.isNull()))
                .orderBy(RENTALS.CREATED_AT.desc())
                .fetch()
                .stream()
                .map(this::toDomainWithVehicleLookup)
                .collect(Collectors.toList());
    }

    /**
     * Konversi data rental menjadi format yang telah ditentukan di package `models`
     *
     * @param record Data polos dari database
     *
     * @return Data yang sudah disesuaikan
     */
    private Rental toDomainWithVehicleLookup(RentalsRecord record) {
        VehiclesRecord v = db.selectFrom(VEHICLES)
                .where(VEHICLES.ID.eq(record.getVehicleId())
                        .and(VEHICLES.DELETED_AT.isNull()))
                .fetchOne();

        VehicleBase vehicle = (v != null) ? vehicleFromRecord(v) : null;

        return toDomain(record, vehicle);
    }

    /**
     * Konversi data rental menjadi format yang telah ditentukan di package `models`
     *
     * @param record Data polos dari database
     * @param vehicle Data kendaraan
     *
     * @return Data yang sudah disesuaikan
     */
    private Rental toDomain(RentalsRecord record, VehicleBase vehicle) {
        return new Rental(
                record.getId(),
                vehicle,
                record.getRenterName(),
                record.getRenterIdNumber(),
                record.getRenterPhoneNumber(),
                record.getStartDate(),
                record.getEndDate(),
                toDomainStatus(record.getStatus()),
                record.getCreatedAt(),
                record.getUpdatedAt(),
                record.getDeletedAt()
        );
    }

    /**
     * Konversi status rental menjadi format yang dapat digunakan oleh aplikasi
     *
     * @param dbStatus Status rental dalam bentuk polos
     *
     * @return Status rental dalam bentuk yang sudah disesuaikan
     */
    private RentalStatus toDomainStatus(com.zef.vehiclerenter.jooq.enums.RentalStatus dbStatus) {
        if (dbStatus == null) {
            return RentalStatus.PENDING;
        }

        return switch (dbStatus) {
            case RENTING -> RentalStatus.RENTING;
            case RETURNED -> RentalStatus.RETURNED;
            case REJECTED -> RentalStatus.REJECTED;
            default -> RentalStatus.PENDING;
        };
    }

    /**
     * Konversi status rental dari bentuk yang bisa digunakan aplikasi menjadi bentuk yang dapat
     * dibaca oleh database
     *
     * @param status Status rental dalam bentuk yang bisa dibaca oleh aplikasi
     *
     * @return Status rental dalam bentuk yang bisa dibaca oleh database
     */
    private com.zef.vehiclerenter.jooq.enums.RentalStatus toDbStatus(RentalStatus status) {
        if (status == null) {
            return com.zef.vehiclerenter.jooq.enums.RentalStatus.PENDING;
        }

        return switch (status) {
            case RENTING -> com.zef.vehiclerenter.jooq.enums.RentalStatus.RENTING;
            case RETURNED -> com.zef.vehiclerenter.jooq.enums.RentalStatus.RETURNED;
            case REJECTED -> com.zef.vehiclerenter.jooq.enums.RentalStatus.REJECTED;
            default -> com.zef.vehiclerenter.jooq.enums.RentalStatus.PENDING;
        };
    }

    /**
     * Konversi data kendaraan polos dari database agar bisa digunakan oleh aplikasi
     *
     * @param record Data kendaraan polos dari database
     *
     * @return Data kendaraan yang sudah disesuaikan agar bisa digunakan oleh aplikasi
     */
    private VehicleBase vehicleFromRecord(VehiclesRecord record) {
        // jOOQ enum → domain enum
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
}