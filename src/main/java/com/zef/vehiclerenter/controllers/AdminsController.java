package com.zef.vehiclerenter.controllers;

import com.zef.vehiclerenter.core.AppContext;
import com.zef.vehiclerenter.core.Router;
import com.zef.vehiclerenter.models.Admin;
import com.zef.vehiclerenter.routes.Routes;
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

public class AdminsController {
    @FXML private Label titleLabel;
    @FXML private TableView<Admin> adminTable;
    @FXML private Button addAdminButton;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    @FXML
    public void initialize() {
        // Handler tombol tambah admin
        addAdminButton.setOnAction(e -> handleAddAdmin());

        // Siapkan kolom tabel admin
        TableColumn<Admin, String> nameCol = new TableColumn<>("Nama Lengkap");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Admin, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(250);

        TableColumn<Admin, String> createdAtCol = new TableColumn<>("Tanggal Dibuat");
        createdAtCol.setCellValueFactory(cell -> {
            if (cell.getValue().getCreatedAt() != null) {
                return new SimpleStringProperty(cell.getValue().getCreatedAt().format(DATE_FORMATTER));
            }
            return new SimpleStringProperty("");
        });
        createdAtCol.setPrefWidth(180);

        // Kolom aksi dengan tombol Edit
        TableColumn<Admin, Void> actionCol = new TableColumn<>("Aksi");
        actionCol.setPrefWidth(120);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final HBox container = new HBox(editButton);

            {
                container.setAlignment(Pos.CENTER);

                // Style tombol Edit
                editButton.setStyle("-fx-background-color: #6c757d; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 16 6 16;");
                editButton.setOnAction(e -> {
                    Admin admin = getTableView().getItems().get(getIndex());
                    handleEditAdmin(admin);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });

        adminTable.getColumns().setAll(nameCol, emailCol, createdAtCol, actionCol);

        loadAdmins();
    }

    /**
     * Muat data admin dari database
     */
    private void loadAdmins() {
        List<Admin> admins = AppContext.get().admins().getAll();
        ObservableList<Admin> items = FXCollections.observableArrayList(admins);
        adminTable.setItems(items);
    }

    /**
     * Handler untuk tombol tambah admin baru
     */
    private void handleAddAdmin() {
        AppContext.get().setSelectedAdminId(null);
        Router.get().navigate(Routes.ADMIN_FORM);
    }

    /**
     * Handler untuk tombol edit admin
     */
    private void handleEditAdmin(Admin admin) {
        AppContext.get().setSelectedAdminId(admin.getId());
        Router.get().navigate(Routes.ADMIN_FORM);
    }

    /**
     * Refresh tabel admin
     */
    public void refresh() {
        loadAdmins();
    }
}
