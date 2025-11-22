package com.zef.vehiclerenter.controllers;

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
        // Logika inisialisasi di sini (jika diperlukan)
    }

    // Handler tombol login akan diimplementasikan nanti
}
