package com.zef.vehiclerenter.controllers;

import com.zef.vehiclerenter.core.AppContext;
import com.zef.vehiclerenter.models.vehicles.VehicleBase;
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

    @FXML
    public void initialize() {
        // Siapkan kolom tabel kendaraan
        TableColumn<VehicleBase, String> nameCol = new TableColumn<>("Nama");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<VehicleBase, String> plateCol = new TableColumn<>("Plat Nomor");
        plateCol.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        plateCol.setPrefWidth(120);

        TableColumn<VehicleBase, String> typeCol = new TableColumn<>("Tipe");
        typeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType().name()));
        typeCol.setPrefWidth(100);

        TableColumn<VehicleBase, String> rateCol = new TableColumn<>("Tarif Harian");
        rateCol.setCellValueFactory(cell -> {
            java.math.BigDecimal rate = cell.getValue().getDailyRate();
            return new SimpleStringProperty(String.format("Rp %.0f", rate.doubleValue()));
        });
        rateCol.setPrefWidth(150);

        vehicleTable.getColumns().setAll(nameCol, plateCol, typeCol, rateCol);

        // Muat data kendaraan dari AppContext
        List<VehicleBase> vehicles = AppContext.get().vehicles().getAll();
        ObservableList<VehicleBase> items = FXCollections.observableArrayList(vehicles);
        vehicleTable.setItems(items);
    }
}
