package com.zef.vehiclerenter.controllers;

import com.zef.vehiclerenter.core.AppContext;
import com.zef.vehiclerenter.models.Rental;
import com.zef.vehiclerenter.models.RentalStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RentalsController {
    @FXML private Label titleLabel;
    @FXML private TableView<Rental> rentalTable;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    @FXML
    public void initialize() {
        // Siapkan kolom tabel rental
        // Kolom Nama Penyewa
        TableColumn<Rental, String> renterNameCol = new TableColumn<>("Nama Penyewa");
        renterNameCol.setCellValueFactory(new PropertyValueFactory<>("renterName"));
        renterNameCol.setPrefWidth(150);

        // Kolom NIK Penyewa
        TableColumn<Rental, String> renterIdCol = new TableColumn<>("NIK");
        renterIdCol.setCellValueFactory(new PropertyValueFactory<>("renterIdNumber"));
        renterIdCol.setPrefWidth(120);

        // Kolom No. HP Penyewa
        TableColumn<Rental, String> renterPhoneCol = new TableColumn<>("No. HP");
        renterPhoneCol.setCellValueFactory(new PropertyValueFactory<>("renterPhoneNumber"));
        renterPhoneCol.setPrefWidth(120);

        // Kolom Kendaraan
        TableColumn<Rental, String> vehicleCol = new TableColumn<>("Kendaraan");
        vehicleCol.setCellValueFactory(cell -> {
            if (cell.getValue().getVehicle() != null) {
                // Contoh output: "Toyota Avanza (B 1234 CD)"
                String name = cell.getValue().getVehicle().getName();
                String plate = cell.getValue().getVehicle().getPlateNumber();
                return new SimpleStringProperty(name + " (" + plate + ")");
            }
            return new SimpleStringProperty("N/A");
        });
        vehicleCol.setPrefWidth(150);

        // Kolom Status Sewa
        TableColumn<Rental, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> {
            RentalStatus status = cell.getValue().getStatus();
            String statusText = switch (status) {
                case PENDING -> "Pending";
                case RENTING -> "Sedang Disewa";
                case RETURNED -> "Dikembalikan";
                case REJECTED -> "Ditolak";
            };
            return new SimpleStringProperty(statusText);
        });
        statusCol.setPrefWidth(120);

        // Kolom Tanggal Diajukan
        TableColumn<Rental, String> createdAtCol = new TableColumn<>("Tanggal Diajukan");
        createdAtCol.setCellValueFactory(cell -> {
            if (cell.getValue().getCreatedAt() != null) {
                return new SimpleStringProperty(cell.getValue().getCreatedAt().format(DATE_FORMATTER));
            }
            return new SimpleStringProperty("");
        });
        createdAtCol.setPrefWidth(150);

        // Kolom aksi dengan tombol sesuai status rental
        TableColumn<Rental, Void> actionCol = new TableColumn<>("Aksi");
        actionCol.setPrefWidth(200);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveButton = new Button("Setujui");
            private final Button rejectButton = new Button("Tolak");
            private final Button returnButton = new Button("Selesai");
            private final HBox pendingContainer = new HBox(8, approveButton, rejectButton);
            private final HBox rentingContainer = new HBox(8, returnButton);

            {
                pendingContainer.setAlignment(Pos.CENTER);
                rentingContainer.setAlignment(Pos.CENTER);

                // Style tombol Setujui
                approveButton.setStyle("-fx-background-color: #2E294E; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 12 6 12;");
                approveButton.setOnAction(e -> {
                    Rental rental = getTableView().getItems().get(getIndex());
                    handleApprove(rental);
                });

                // Style tombol Tolak
                rejectButton.setStyle("-fx-background-color: #dc3545; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 12 6 12;");
                rejectButton.setOnAction(e -> {
                    Rental rental = getTableView().getItems().get(getIndex());
                    handleReject(rental);
                });

                // Style tombol Kembalikan
                returnButton.setStyle("-fx-background-color: #2E294E; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 12 6 12;");
                returnButton.setOnAction(e -> {
                    Rental rental = getTableView().getItems().get(getIndex());
                    handleReturn(rental);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0) {
                    setGraphic(null);
                } else {
                    Rental rental = getTableView().getItems().get(getIndex());
                    RentalStatus status = rental.getStatus();

                    // Tampilkan tombol sesuai status
                    if (status == RentalStatus.PENDING) {
                        setGraphic(pendingContainer);
                    } else if (status == RentalStatus.RENTING) {
                        setGraphic(rentingContainer);
                    } else {
                        // Untuk status RETURNED atau REJECTED, tidak ada tombol
                        setGraphic(null);
                    }
                }
            }
        });

        rentalTable.getColumns().setAll(renterNameCol, renterIdCol, renterPhoneCol, vehicleCol, statusCol, createdAtCol, actionCol);

        loadRentals();
    }

    /**
     * Muat data rental dari database
     */
    private void loadRentals() {
        List<Rental> rentals = AppContext.get().rentals().getAll();
        ObservableList<Rental> items = FXCollections.observableArrayList(rentals);
        rentalTable.setItems(items);
    }

    /**
     * Setujui rental (ubah status dari PENDING ke RENTING)
     */
    private void handleApprove(Rental rental) {
        try {
            AppContext.get().rentals().updateStatus(rental.getId(), RentalStatus.RENTING);
            // Refresh tabel dan beritahu controller lain
            loadRentals();
            AppContext.get().notifyDataChanged();
        } catch (Exception e) {
            System.err.println("Gagal menyetujui rental: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tolak rental (ubah status dari PENDING ke REJECTED)
     */
    private void handleReject(Rental rental) {
        try {
            AppContext.get().rentals().updateStatus(rental.getId(), RentalStatus.REJECTED);
            // Refresh tabel dan beritahu controller lain
            loadRentals();
            AppContext.get().notifyDataChanged();
        } catch (Exception e) {
            System.err.println("Gagal menolak rental: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Kembalikan kendaraan (ubah status dari RENTING ke RETURNED)
     */
    private void handleReturn(Rental rental) {
        try {
            AppContext.get().rentals().updateStatus(rental.getId(), RentalStatus.RETURNED);
            // Refresh tabel dan beritahu controller lain
            loadRentals();
            AppContext.get().notifyDataChanged();
        } catch (Exception e) {
            System.err.println("Gagal mengembalikan kendaraan: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
