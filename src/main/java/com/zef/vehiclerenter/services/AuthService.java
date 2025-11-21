package com.zef.vehiclerenter.services;

import com.zef.vehiclerenter.jooq.tables.records.AdminsRecord;
import com.zef.vehiclerenter.models.Admin;
import org.jooq.DSLContext;

import static com.zef.vehiclerenter.jooq.tables.Admins.ADMINS;

public class AuthService {
    private final DSLContext db;
    private final AdminService adminService;

    public AuthService(DSLContext db, AdminService adminService) {
        this.db = db;
        this.adminService = adminService;
    }

    /**
     * Coba login dengan email + password.
     * @return Data admin jika sukses, `null` jika gagal.
     */
    public Admin tryLogin(String email, String rawPassword) {
        // Ambil record admin lengkap (termasuk password_hash)
        AdminsRecord record = db.selectFrom(ADMINS)
                .where(ADMINS.EMAIL.eq(email)
                .and(ADMINS.DELETED_AT.isNull()))
                .fetchOne();

        if (record == null) {
            return null;
        }

        String storedHash = record.getPasswordHash();
        if (!PasswordHasher.verify(rawPassword, storedHash)) {
            return null;
        }

        // Konversi ke domain Admin (tanpa password)
        return adminService.findById(record.getId());
    }

    /**
     * Implementasi hashing sederhana
     */
    private static class PasswordHasher {
        /**
         * Konversi input password polos menjadi hash yang sudah dienkripsi
         *
         * @param raw Input password polos (masih bisa dibaca manusia)
         *
         * @return Password yang sudah dienkripsi
         */
        static String hash(String raw) {
            return Integer.toHexString(raw.hashCode());
        }

        /**
         * Cek apakah password yang dimasukkan sama dengan password yang sudah disimpan dalam bentuk hash
         *
         * @param raw Inputan password
         * @param storedHash Password yang tersimpan
         *
         * @return `true` jika benar, `false` jika salah/gagal
         */
        static boolean verify(String raw, String storedHash) {
            return storedHash != null && storedHash.equals(hash(raw));
        }
    }
}