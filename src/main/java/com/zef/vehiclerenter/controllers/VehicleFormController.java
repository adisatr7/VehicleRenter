package com.zef.vehiclerenter.controllers;

import com.zef.vehiclerenter.core.AppContext;
import com.zef.vehiclerenter.core.Router;
import com.zef.vehiclerenter.models.vehicles.Bike;
import com.zef.vehiclerenter.models.vehicles.Car;
import com.zef.vehiclerenter.models.vehicles.VehicleBase;
import com.zef.vehiclerenter.routes.Routes;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.UUID;

public class VehicleFormController {
    @FXML private Label titleLabel;
    @FXML private Hyperlink backButton;
    @FXML private ComboBox<String> vehicleTypeCombo;
    @FXML private TextField nameField;
    @FXML private TextField plateNumberField;
    @FXML private TextField dailyRateField;

    // Car fields
    @FXML private javafx.scene.layout.VBox carFieldsContainer;
    @FXML private TextField seatCountField;
    @FXML private TextField doorCountField;
    @FXML private CheckBox hasAutoTransmissionCheck;

    // Bike fields
    @FXML private javafx.scene.layout.VBox bikeFieldsContainer;
    @FXML private TextField engineCcField;
    @FXML private CheckBox hasTopBoxCheck;

    @FXML private Label errorLabel;
    @FXML private Button deleteButton;
    @FXML private Button saveButton;

    private UUID editingVehicleId = null;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Inisialisasi combo box tipe kendaraan
        vehicleTypeCombo.getItems().addAll("Motor", "Mobil");

        // Listener untuk mengubah tampilan field berdasarkan tipe kendaraan
        vehicleTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateFieldsVisibility(newVal);
            }
        });

        // Handler tombol kembali
        backButton.setOnAction(e -> Router.get().navigate(Routes.VEHICLES));

        // Handler tombol simpan
        saveButton.setOnAction(e -> handleSave());

        // Handler tombol hapus
        deleteButton.setOnAction(e -> handleDelete());

        // Cek apakah ada kendaraan yang dipilih untuk diedit
        UUID selectedId = AppContext.get().getSelectedVehicleId();
        if (selectedId != null) {
            loadVehicleForEdit(selectedId);
        }
    }

    /**
     * Tampilkan atau sembunyikan field berdasarkan tipe kendaraan yang dipilih
     */
    private void updateFieldsVisibility(String vehicleType) {
        if ("Mobil".equals(vehicleType)) {
            carFieldsContainer.setVisible(true);
            carFieldsContainer.setManaged(true);
            bikeFieldsContainer.setVisible(false);
            bikeFieldsContainer.setManaged(false);
        } else if ("Motor".equals(vehicleType)) {
            bikeFieldsContainer.setVisible(true);
            bikeFieldsContainer.setManaged(true);
            carFieldsContainer.setVisible(false);
            carFieldsContainer.setManaged(false);
        } else {
            carFieldsContainer.setVisible(false);
            carFieldsContainer.setManaged(false);
            bikeFieldsContainer.setVisible(false);
            bikeFieldsContainer.setManaged(false);
        }
    }

    /**
     * Muat data kendaraan untuk mode edit
     */
    private void loadVehicleForEdit(UUID vehicleId) {
        VehicleBase vehicle = AppContext.get().vehicles().getById(vehicleId);
        if (vehicle == null) {
            showError("Kendaraan tidak ditemukan");
            return;
        }

        isEditMode = true;
        editingVehicleId = vehicleId;

        // Ubah judul dan tampilkan tombol hapus
        titleLabel.setText("Edit Kendaraan");
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);

        // Isi field dengan data kendaraan
        nameField.setText(vehicle.getName());
        plateNumberField.setText(vehicle.getPlateNumber());
        dailyRateField.setText(vehicle.getDailyRate().toString());

        // Set tipe kendaraan dan isi field spesifik
        if (vehicle instanceof Car) {
            vehicleTypeCombo.setValue("Mobil");
            Car car = (Car) vehicle;
            seatCountField.setText(String.valueOf(car.getSeatCount()));
            doorCountField.setText(String.valueOf(car.getDoorCount()));
            hasAutoTransmissionCheck.setSelected(car.isHasAutoTransmission());
        } else if (vehicle instanceof Bike) {
            vehicleTypeCombo.setValue("Motor");
            Bike bike = (Bike) vehicle;
            engineCcField.setText(String.valueOf(bike.getEngineCc()));
            hasTopBoxCheck.setSelected(bike.isHasTopBox());
        }

        // Disable combo box saat edit (tidak bisa ubah tipe)
        vehicleTypeCombo.setDisable(true);
    }

    /**
     * Handler untuk tombol simpan
     */
    private void handleSave() {
        // Validasi input
        String name = nameField.getText().trim();
        String plateNumber = plateNumberField.getText().trim();
        String dailyRateStr = dailyRateField.getText().trim();
        String vehicleType = vehicleTypeCombo.getValue();

        if (name.isEmpty() || plateNumber.isEmpty() || dailyRateStr.isEmpty() || vehicleType == null) {
            showError("Mohon isi semua field yang wajib");
            return;
        }

        // Parse tarif harian
        BigDecimal dailyRate;
        try {
            dailyRate = new BigDecimal(dailyRateStr);
            if (dailyRate.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Tarif harian harus lebih dari 0");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Tarif harian harus berupa angka");
            return;
        }

        // Buat atau update kendaraan
        try {
            VehicleBase vehicle;
            // Untuk mode edit gunakan ID yang ada, untuk mode tambah gunakan null
            UUID id = isEditMode ? editingVehicleId : null;

            if ("Mobil".equals(vehicleType)) {
                // Parse car-specific fields
                int seatCount = parseIntField(seatCountField, "Jumlah kursi", 2);
                int doorCount = parseIntField(doorCountField, "Jumlah pintu", 2);
                boolean hasAutoTransmission = hasAutoTransmissionCheck.isSelected();

                vehicle = new Car(id, name, plateNumber, dailyRate, seatCount, doorCount, hasAutoTransmission);
            } else {
                // Parse bike-specific fields
                int engineCc = parseIntField(engineCcField, "Kapasitas mesin", 125);
                boolean hasTopBox = hasTopBoxCheck.isSelected();

                vehicle = new Bike(id, name, plateNumber, dailyRate, engineCc, hasTopBox);
            }

            // Simpan ke database
            AppContext.get().vehicles().save(vehicle);

            // Beritahu controller lain bahwa data telah berubah
            AppContext.get().notifyDataChanged();

            // Kembali ke halaman daftar kendaraan
            Router.get().navigate(Routes.VEHICLES);

        } catch (Exception e) {
            showError("Gagal menyimpan kendaraan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Parse field integer dengan nilai default
     */
    private int parseIntField(TextField field, String fieldName, int defaultValue) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            return defaultValue;
        }
        try {
            int result = Integer.parseInt(value);
            if (result <= 0) {
                showError(fieldName + " harus lebih dari 0");
                throw new IllegalArgumentException(fieldName + " harus lebih dari 0");
            }
            return result;
        } catch (NumberFormatException e) {
            showError(fieldName + " harus berupa angka");
            throw new IllegalArgumentException(fieldName + " harus berupa angka");
        }
    }

    /**
     * Handler untuk tombol hapus
     */
    private void handleDelete() {
        if (!isEditMode || editingVehicleId == null) {
            return;
        }

        // Konfirmasi penghapusan
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Kendaraan");
        alert.setContentText("Apakah Anda yakin ingin menghapus kendaraan ini?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    AppContext.get().vehicles().delete(editingVehicleId);
                    AppContext.get().notifyDataChanged();
                    Router.get().navigate(Routes.VEHICLES);
                } catch (Exception e) {
                    showError("Gagal menghapus kendaraan: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
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
