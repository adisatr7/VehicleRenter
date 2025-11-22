package com.zef.vehiclerenter.controllers;

import com.zef.vehiclerenter.core.AppContext;
import com.zef.vehiclerenter.core.Router;
import com.zef.vehiclerenter.models.Admin;
import com.zef.vehiclerenter.routes.Routes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.UUID;

public class AdminFormController {
    @FXML private Label titleLabel;
    @FXML private Hyperlink backButton;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;

    private UUID editingAdminId = null;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Handler tombol kembali
        backButton.setOnAction(e -> Router.get().navigate(Routes.ADMINS));

        // Handler tombol simpan
        saveButton.setOnAction(e -> handleSave());

        // Cek apakah ada admin yang dipilih untuk diedit
        UUID selectedId = AppContext.get().getSelectedAdminId();
        if (selectedId != null) {
            loadAdminForEdit(selectedId);
        }
    }

    /**
     * Muat data admin untuk mode edit
     */
    private void loadAdminForEdit(UUID adminId) {
        Admin admin = AppContext.get().admins().findById(adminId);
        if (admin == null) {
            showError("Admin tidak ditemukan");
            return;
        }

        isEditMode = true;
        editingAdminId = adminId;

        // Ubah judul
        titleLabel.setText("Edit Admin");

        // Isi field dengan data admin
        nameField.setText(admin.getName());
        emailField.setText(admin.getEmail());

        // Untuk edit, password tidak wajib diisi (hanya jika ingin diubah)
        passwordField.setPromptText("Kosongkan jika tidak ingin mengubah password");
        confirmPasswordField.setPromptText("Kosongkan jika tidak ingin mengubah password");
    }

    /**
     * Handler untuk tombol simpan
     */
    private void handleSave() {
        // Validasi input
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty()) {
            showError("Nama dan email wajib diisi");
            return;
        }

        // Validasi email format sederhana
        if (!email.contains("@") || !email.contains(".")) {
            showError("Format email tidak valid");
            return;
        }

        // Untuk mode tambah, password wajib diisi
        if (!isEditMode && (password.isEmpty() || confirmPassword.isEmpty())) {
            showError("Password dan konfirmasi password wajib diisi");
            return;
        }

        // Jika password diisi, validasi konfirmasi
        if (!password.isEmpty() || !confirmPassword.isEmpty()) {
            if (!password.equals(confirmPassword)) {
                showError("Password dan konfirmasi password tidak sama");
                return;
            }

            if (password.length() < 6) {
                showError("Password minimal 6 karakter");
                return;
            }
        }

        try {
            if (isEditMode) {
                // Mode edit - update admin yang sudah ada
                updateAdmin(editingAdminId, name, email, password);
            } else {
                // Mode tambah - buat admin baru
                createAdmin(name, email, password);
            }

            // Kembali ke halaman daftar admin
            Router.get().navigate(Routes.ADMINS);

        } catch (Exception e) {
            showError("Gagal menyimpan admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Buat admin baru
     */
    private void createAdmin(String name, String email, String password) {
        // Cek apakah email sudah digunakan
        Admin existingAdmin = AppContext.get().admins().findByEmail(email);
        if (existingAdmin != null) {
            showError("Email sudah digunakan oleh admin lain");
            return;
        }

        // Hash password
        String passwordHash = hashPassword(password);

        // Buat admin baru
        UUID newId = UUID.randomUUID();
        Admin newAdmin = new Admin(newId, email, name);
        AppContext.get().admins().create(newAdmin, passwordHash);
    }

    /**
     * Update admin yang sudah ada
     */
    private void updateAdmin(UUID adminId, String name, String email, String password) {
        // Cek apakah email sudah digunakan oleh admin lain
        Admin existingAdmin = AppContext.get().admins().findByEmail(email);
        if (existingAdmin != null && !existingAdmin.getId().equals(adminId)) {
            showError("Email sudah digunakan oleh admin lain");
            return;
        }

        // Update data admin
        AppContext.get().admins().update(adminId, name, email);

        // Jika password diisi, update password
        if (!password.isEmpty()) {
            String passwordHash = hashPassword(password);
            AppContext.get().admins().updatePassword(adminId, passwordHash);
        }
    }

    /**
     * Hash password sederhana (sama dengan yang ada di AuthService)
     */
    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }

    /**
     * Tampilkan pesan error
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
