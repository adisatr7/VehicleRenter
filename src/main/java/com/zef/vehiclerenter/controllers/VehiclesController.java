package com.zef.vehiclerenter.controllers;

import com.zef.vehiclerenter.core.AppContext;
import com.zef.vehiclerenter.core.Router;
import com.zef.vehiclerenter.routes.Routes;
import com.zef.vehiclerenter.models.vehicles.VehicleBase;
import com.zef.vehiclerenter.models.vehicles.Car;
import com.zef.vehiclerenter.models.vehicles.Bike;
import com.zef.vehiclerenter.models.Rental;
import com.zef.vehiclerenter.models.RentalStatus;
import javafx.scene.control.TableRow;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import java.text.NumberFormat;
import java.util.Locale;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class VehiclesController {
    @FXML private Label titleLabel;
    @FXML private TableView<VehicleBase> vehicleTable;
    @FXML private Button addVehicleButton;
    @FXML private Button refreshButton;

    @FXML
    public void initialize() {
        loadVehicles();

        // Tombol Tambah Kendaraan hanya untuk admin
        addVehicleButton.visibleProperty().bind(AppContext.get().currentAdminProperty().isNotNull());
        addVehicleButton.managedProperty().bind(addVehicleButton.visibleProperty());

        // Handler tombol refresh
        refreshButton.setOnAction(e -> refresh());

        // Handler tombol tambah kendaraan
        addVehicleButton.setOnAction(e -> handleAddVehicle());

        // Listen for data changes and refresh table when notified
        AppContext.get().dataVersionProperty().addListener((obs, oldVal, newVal) -> {
            refresh();
        });
    }

    /**
     * Tombol tambah kendaraan baru (khusus admin)
     */
    private void handleAddVehicle() {
        AppContext.get().setSelectedVehicleId(null);
        Router.get().navigate(Routes.VEHICLE_FORM);
    }

    /**
     * Ambil data kendaraan dari database dan tampilkan di tabel
     */
    private void loadVehicles() {
        // Muat data kendaraan dan rental terlebih dahulu
        List<VehicleBase> vehicles = AppContext.get().vehicles().getAll();
        List<Rental> rentals = AppContext.get().rentals().getAll();

        // Hitung kumpulan ID kendaraan yang saat ini berstatus RENTING
        final Set<java.util.UUID> rentedIds = rentals.stream()
                .filter(r -> r != null && r.getStatus() == RentalStatus.RENTING)
                .map(Rental::getVehicle)
                .filter(Objects::nonNull)
                .map(VehicleBase::getId)
                .collect(Collectors.toSet());

        // Hitung kumpulan ID kendaraan yang saat ini berstatus PENDING (booked)
        final Set<java.util.UUID> bookedIds = rentals.stream()
                .filter(r -> r != null && r.getStatus() == RentalStatus.PENDING)
                .map(Rental::getVehicle)
                .filter(Objects::nonNull)
                .map(VehicleBase::getId)
                .collect(Collectors.toSet());

        // Siapkan kolom tabel kendaraan
        // Kolom nama dan plat kendaraan
        TableColumn<VehicleBase, String> nameCol = new TableColumn<>("Nama");
        nameCol.setCellValueFactory(cell -> {
            String name = cell.getValue().getName();
            String plate = cell.getValue().getPlateNumber();
            return new SimpleStringProperty(name + " (" + plate + ")");
        });
        nameCol.setPrefWidth(200);

        // Kolom tipe kendaraan
        TableColumn<VehicleBase, String> typeCol = new TableColumn<>("Tipe");
        typeCol.setCellValueFactory(cell -> {
            // Konversi enum menjadi nama yang lebih ramah pengguna
            String typeName = cell.getValue().getType().name();
            if (typeName == "BIKE") {
                typeName = "Motor";
            } else if (typeName == "CAR") {
                typeName = "Mobil";
            }
            return new SimpleStringProperty(typeName);
        });
        typeCol.setPrefWidth(100);

        // Kolom tarif
        TableColumn<VehicleBase, String> rateCol = new TableColumn<>("Tarif Harian");
        rateCol.setCellValueFactory(cell -> {
            // Format mata uang ke Rupiah Indonesia, lebih mudah dibaca dengan pemisah ribuan dan tanpa desimal
            NumberFormat rupiahFmt = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
            rupiahFmt.setMaximumFractionDigits(0);
            java.math.BigDecimal rate = cell.getValue().getDailyRate();
            String formatted = rupiahFmt.format(rate);
            if (!formatted.startsWith("Rp")) {
                formatted = "Rp " + formatted.replaceAll("[^0-9,\\.]", "");
            }
            return new SimpleStringProperty(formatted);
        });
        rateCol.setPrefWidth(150);

        // Kolom ketersediaan
        TableColumn<VehicleBase, String> availabilityCol = new TableColumn<>("Ketersediaan");
        availabilityCol.setCellValueFactory(cell -> {
            java.util.UUID vehicleId = cell.getValue().getId();
            if (rentedIds.contains(vehicleId)) {
                return new SimpleStringProperty("Disewa");
            } else if (bookedIds.contains(vehicleId)) {
                return new SimpleStringProperty("Booked");
            } else {
                return new SimpleStringProperty("Tersedia");
            }
        });
        availabilityCol.setPrefWidth(120);

        // Kolom keterangan (atribut spesifik kendaraan)
        TableColumn<VehicleBase, String> descriptionCol = new TableColumn<>("Keterangan");
        descriptionCol.setCellValueFactory(cell -> {
            VehicleBase vehicle = cell.getValue();
            if (vehicle instanceof Car) {
                // Untuk mobil, tampilkan jumlah pintu, kursi, dan transmisi
                Car car = (Car) vehicle;
                String transmission = car.isHasAutoTransmission() ? "Matic" : "Manual";
                return new SimpleStringProperty(
                    car.getDoorCount() + " pintu, " +
                    car.getSeatCount() + " kursi, " +
                    transmission
                );
            } else if (vehicle instanceof Bike) {
                // Untuk motor, tampilkan cc dan apakah ada top box
                Bike bike = (Bike) vehicle;
                String topBox = bike.isHasTopBox()
                    ? "ada bagasi tambahan"
                    : "tidak ada bagasi tambahan";
                return new SimpleStringProperty(
                    "CC " + bike.getEngineCc() + ", " + topBox
                );
            }
            return new SimpleStringProperty("-");
        });
        descriptionCol.setPrefWidth(250);

        // Kolom aksi dengan tombol Sewa dan Edit (admin-only)
        TableColumn<VehicleBase, Void> actionCol = new TableColumn<>("Aksi");
        actionCol.setPrefWidth(180);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button rentButton = new Button("Sewa");
            private final Button editButton = new Button("Edit");
            private final HBox container = new HBox(8, rentButton, editButton);

            {
                container.setAlignment(Pos.CENTER);

                // Tombol Sewa
                rentButton.setStyle("-fx-background-color: #2E294E; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 12 6 12;");
                rentButton.setOnAction(e -> {
                    VehicleBase vehicle = getTableView().getItems().get(getIndex());
                    AppContext.get().setSelectedVehicleId(vehicle.getId());
                    Router.get().navigate(Routes.RENT_FORM);
                });

                // Tombol Edit
                editButton.setStyle("-fx-background-color: #7b7b7bff; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 12 6 12;");
                editButton.setOnAction(e -> {
                    VehicleBase vehicle = getTableView().getItems().get(getIndex());
                    AppContext.get().setSelectedVehicleId(vehicle.getId());
                    Router.get().navigate(Routes.VEHICLE_FORM);
                });

                // Tombol Edit hanya untuk admin saja
                editButton.visibleProperty().bind(AppContext.get().currentAdminProperty().isNotNull());
                editButton.managedProperty().bind(editButton.visibleProperty());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0) {
                    setGraphic(null);
                } else {
                    VehicleBase vehicle = getTableView().getItems().get(getIndex());
                    // Sembunyikan tombol Sewa jika kendaraan tidak tersedia (disewa atau booked)
                    boolean isUnavailable = rentedIds.contains(vehicle.getId()) || bookedIds.contains(vehicle.getId());
                    rentButton.setVisible(!isUnavailable);
                    rentButton.setManaged(!isUnavailable);
                    setGraphic(container);
                }
            }
        });

        // Muat data kendaraan dari AppContext
        vehicleTable.getColumns().setAll(nameCol, typeCol, rateCol, descriptionCol, availabilityCol, actionCol);
        ObservableList<VehicleBase> items = FXCollections.observableArrayList(vehicles);
        vehicleTable.setItems(items);

        // Warnai baris yang sedang disewa atau booked menjadi abu-abu
        vehicleTable.setRowFactory(tv -> new TableRow<VehicleBase>() {
            @Override
            protected void updateItem(VehicleBase item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (rentedIds.contains(item.getId()) || bookedIds.contains(item.getId())) {
                    // Untuk baris yang tidak tersedia (disewa/booked), buat seleksi terlihat sama dengan unselected
                    setStyle("-fx-background-color: #e8e8e8; -fx-selection-bar: #e8e8e8; -fx-selection-bar-non-focused: #e8e8e8;");
                } else {
                    // Untuk baris yang tersedia, gunakan warna ungu untuk seleksi
                    setStyle("-fx-selection-bar: #9990C9; -fx-selection-bar-non-focused: #9990C9;");
                }
            }
        });
    }

    /**
     * Muat ulang data kendaraan dari database (dipanggil setelah perubahan data rental)
     */
    public void refresh() {
        vehicleTable.getColumns().clear();
        vehicleTable.getItems().clear();
        loadVehicles();
    }
}
