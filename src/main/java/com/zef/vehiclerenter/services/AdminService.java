package com.zef.vehiclerenter.services;

import com.zef.vehiclerenter.jooq.tables.records.AdminsRecord;
import com.zef.vehiclerenter.models.Admin;
import org.jooq.DSLContext;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.zef.vehiclerenter.jooq.tables.Admins.ADMINS;

public class AdminService {
    private final DSLContext db;

    public AdminService(DSLContext db) {
        this.db = db;
    }

    /**
     * Ambil data user admin berdasarkan ID
     *
     * @param id ID admin yang dibutuhkan
     *
     * @return Data lengkap admin TIDAK termasuk password
     */
    public Admin findById(UUID id) {
        var record = db.selectFrom(ADMINS)
                .where(ADMINS.ID.eq(id)
                .and(ADMINS.DELETED_AT.isNull()))
                .fetchOne();

        return (record != null)
                ? toDomain(record)
                : null;
    }

    /**
     * Cari admin berdasarkan email
     *
     * @param email Email admin
     *
     * @return Data lengkap admin TIDAK termasuk password
     */
    public Admin findByEmail(String email) {
        var record = db.selectFrom(ADMINS)
                .where(ADMINS.EMAIL.eq(email)
                .and(ADMINS.DELETED_AT.isNull()))
                .fetchOne();

        return (record != null)
                ? toDomain(record)
                : null;
    }

    /**
     * Ambil semua data admin TIDAK termasuk password
     *
     * @return Daftar akun admin
     */
    public List<Admin> getAll() {
        return db.selectFrom(ADMINS)
                .where(ADMINS.DELETED_AT.isNull())
                .orderBy(ADMINS.FULLNAME.asc())
                .fetch()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Simpan data admin baru ke database
     *
     * @param admin Data admin baru yang diambil dari kolom inputan
     * @param passwordHash Password yang sudah dienkripsi
     */
    public void create(Admin admin, String passwordHash) {
        db.insertInto(ADMINS)
                .set(ADMINS.ID, admin.getId())
                .set(ADMINS.EMAIL, admin.getEmail())
                .set(ADMINS.FULLNAME, admin.getName())
                .set(ADMINS.PASSWORD_HASH, passwordHash)
                .execute();
    }

    /**
     * Konversi data admin polos dari database agar bisa digunakan oleh aplikasi
     *
     * @param record Data admin polos dari database
     *
     * @return Data yang sudah disesuaikan
     */
    private Admin toDomain(AdminsRecord record) {
        return new Admin(
                record.getId(),
                record.getEmail(),
                record.getFullname(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}
