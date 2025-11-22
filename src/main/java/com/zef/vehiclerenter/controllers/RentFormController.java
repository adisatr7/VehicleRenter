package com.zef.vehiclerenter.controllers;

import com.zef.vehiclerenter.core.AppContext;
import com.zef.vehiclerenter.core.Router;
import com.zef.vehiclerenter.routes.Routes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;

public class RentFormController {
    @FXML private TextField renterNameField;
    @FXML private TextField renterIdNumberField;
    @FXML private TextField renterPhoneField;
    @FXML private Button submitButton;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        submitButton.setOnAction(e -> handleSubmit());
    }

    /**
     * Tombol sewa
     */
    private void handleSubmit() {
        // Ambil data dari form
        String name = renterNameField.getText() == null ? "" : renterNameField.getText().trim();
        String idNumber = renterIdNumberField.getText() == null ? "" : renterIdNumberField.getText().trim();
        String phone = renterPhoneField.getText() == null ? "" : renterPhoneField.getText().trim();

        // Jika ada field wajib yang kosong, tampilkan error
        if (name.isEmpty() || idNumber.isEmpty()) {
            showError("Nama dan NIK wajib diisi.");
            return;
        }

        // Dapatkan kendaraan yang dipilih dari AppContext
        var selected = AppContext.get().selectedVehicleIdProperty().get();
        if (selected == null) {
            showError("Tidak ada kendaraan yang dipilih untuk disewa.");
            return;
        }

        try {
            // Simpan data rental
            AppContext.get().rentals().createRental(selected, name, idNumber, phone);
            AppContext.get().notifyDataChanged();

            // Kembali ke halaman daftar kendaraan
            Router.get().navigate(Routes.VEHICLES);
        } catch (Exception ex) {
            // Handle error saat menyimpan rental
            showError("Gagal membuat sewa: " + ex.getMessage());
        }
    }

    /**
     * Tampilkan pesan error
     * @param msg Pesan error
     */
    private void showError(String msg) {
        Platform.runLater(() -> {
            errorLabel.setText(msg);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        });
    }
}
