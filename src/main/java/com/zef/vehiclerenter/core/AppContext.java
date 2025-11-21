package com.zef.vehiclerenter.core;

import com.zaxxer.hikari.HikariDataSource;
import com.zef.vehiclerenter.models.Admin;
import com.zef.vehiclerenter.services.AuthService;
import com.zef.vehiclerenter.services.VehicleService;
import com.zef.vehiclerenter.services.RentalService;
import com.zef.vehiclerenter.services.AdminService;
import org.jooq.DSLContext;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.binding.BooleanBinding;

import javax.sql.DataSource;

public final class AppContext {
    private static AppContext instance;

    private final HikariDataSource dataSource;
    private final DSLContext db;

    // Service-service disimpan disini agar aplikasi dengan mudah dapat mengaksesnya dari manapun
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final AdminService adminService;
    private final AuthService authService;

    // Menampung data admin yang sedang login (jika ada) sebagai property agar bisa di-bind di UI
    private final ObjectProperty<Admin> currentAdmin = new SimpleObjectProperty<>(null);

    private void setCurrentAdmin(Admin admin) {
        this.currentAdmin.set(admin);
    }

    public Admin getCurrentAdmin() {
        return currentAdmin.get();
    }

    public ObjectProperty<Admin> currentAdminProperty() {
        return currentAdmin;
    }

    // BooleanBinding untuk kemudahan binding di UI (logged in = currentAdmin != null)
    public BooleanBinding loggedInProperty() {
        return currentAdmin.isNotNull();
    }

    /**
     * Cek apakah ada sesi login
     * @return `true` jika ada, `false` jika tidak
     */
    public boolean isLoggedIn() {
        return currentAdmin.get() != null;
    }

    /**
     * Paksa admin untuk login terlebih dahulu
     */
    public void requireAuth() {
        if (currentAdmin == null) {
            throw new IllegalStateException("Admin belum login");
        }
    }

    /**
     * Fungsi login admin yang dipakai di controller
     *
     * @param email Inputan email
     * @param password Inputan password (non-hash)
     *
     * @return `true` jika login berhasil, `false` jika gagal
     */
    public boolean login(String email, String password) {
        Admin admin = authService.tryLogin(email, password);
        if (admin == null) {
            return false;
        }
        this.setCurrentAdmin(admin);
        return true;
    }

    /**
     * Fungsi logout admin
     */
    public void logout() {
        this.setCurrentAdmin(null);
    }

    private AppContext(HikariDataSource dataSource, DSLContext db) {
        this.dataSource = dataSource;
        this.db = db;

        this.vehicleService = new VehicleService(db);
        this.rentalService  = new RentalService(db, vehicleService);
        this.adminService   = new AdminService(db);
        this.authService   = new AuthService(db, adminService);
    }

    /**
     * Inisiasi AppContext. Tujuannya agar aplikasi dapat dengan mudah saling meminjam
     * data antara satu komponen dengan komponen lainnya
     */
    public static void init() {
        if (instance != null) {
            return;
        }

        HikariDataSource ds = DatabaseManager.createDataSource();
        DSLContext ctx = DatabaseManager.createDslContext(ds);

        instance = new AppContext(ds, ctx);
    }

    public static AppContext get() {
        if (instance == null) {
            throw new IllegalStateException("AppContext not initialized. Call AppContext.init() first.");
        }
        return instance;
    }

    public DataSource dataSource() {
        return dataSource;
    }

    public DSLContext db() {
        return db;
    }

    public VehicleService vehicles() {
        return vehicleService;
    }

    public RentalService rentals() {
        return rentalService;
    }

    public AdminService admins() {
        return adminService;
    }

    /**
     * Matikan AppContext. Fungsi ini akan berjalan ketika aplikasi ditutup
     */
    public static void shutdown() {
        if (instance != null && instance.dataSource != null) {
            instance.dataSource.close();
        }
    }
}
