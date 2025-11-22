package com.zef.vehiclerenter.controllers;

import com.zef.vehiclerenter.core.AppContext;
import com.zef.vehiclerenter.core.Router;
import com.zef.vehiclerenter.routes.Routes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private StackPane rootPane;

    @FXML
    public void initialize() {
        // Wire tombol login ke handler
        loginButton.setOnAction(event -> handleLogin());

        // Tekan Enter di password field = submit
        passwordField.setOnAction(event -> handleLogin());
    }

    /**
     * Handler untuk proses login
     */
    private void handleLogin() {
        // Ambil nilai dari field
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validasi input kosong
        if (email.isEmpty() || password.isEmpty()) {
            showError("Email dan password tidak boleh kosong");
            return;
        }

        // Login via AppContext
        boolean success = AppContext.get().login(email, password);

        if (success) {
            // Login berhasil, navigasi ke halaman kendaraan
            Router.get().navigate(Routes.VEHICLES);
        } else {
            // Login gagal, tampilkan pesan error
            showError("Email atau password salah");
        }
    }

    /**
     * Tampilkan pesan error di UI
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
