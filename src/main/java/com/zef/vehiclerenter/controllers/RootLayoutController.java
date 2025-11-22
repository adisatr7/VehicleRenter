package com.zef.vehiclerenter.controllers;

import com.zef.vehiclerenter.core.AppContext;
import com.zef.vehiclerenter.core.Router;
import com.zef.vehiclerenter.routes.Routes;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import com.zef.vehiclerenter.core.AppConfig;

/**
 * Controller untuk layout utama yang memiliki sidebar dan area konten dinamis.
 * Sidebar tetap, sedangkan area konten (sceneContainer) diganti oleh Router.
 */
public class RootLayoutController {
    // Area tempat Router menaruh view
    @FXML private AnchorPane sceneContainer;

    @FXML private Hyperlink sidebarGoToVehiclesHyperlink;
    @FXML private Hyperlink sidebarGoToRentalHyperlink;
    @FXML private Hyperlink sidebarGoToAdminsHyperlink;
    @FXML private Hyperlink sidebarLoginHyperlink;
    @FXML private Hyperlink sidebarLogoutHyperlink;
    @FXML private Label sidebarTitleLabel;
    @FXML private AnchorPane sceneContainerInner;

    @FXML
    public void initialize() {
        if (sidebarTitleLabel != null) {
            sidebarTitleLabel.setText(AppConfig.JUDUL_APLIKASI);
        }
        // Bind visibility: tunjukkan login jika tidak ada admin, sebaliknya logout jika ada admin
        sidebarLoginHyperlink.visibleProperty().bind(AppContext.get().currentAdminProperty().isNull());
        sidebarLoginHyperlink.managedProperty().bind(sidebarLoginHyperlink.visibleProperty());

        sidebarLogoutHyperlink.visibleProperty().bind(AppContext.get().currentAdminProperty().isNotNull());
        sidebarLogoutHyperlink.managedProperty().bind(sidebarLogoutHyperlink.visibleProperty());

        // Sembunyikan navigasi ke halaman Rental jika belum login
        sidebarGoToRentalHyperlink.visibleProperty().bind(AppContext.get().currentAdminProperty().isNotNull());
        sidebarGoToRentalHyperlink.managedProperty().bind(sidebarGoToRentalHyperlink.visibleProperty());

        // Sembunyikan navigasi ke halaman Kelola Admin jika belum login
        sidebarGoToAdminsHyperlink.visibleProperty().bind(AppContext.get().currentAdminProperty().isNotNull());
        sidebarGoToAdminsHyperlink.managedProperty().bind(sidebarGoToAdminsHyperlink.visibleProperty());
    }

    /**
     * Fungsi untuk membuka halaman kendaraan
     */
    public void handleGoToVehiclesView() {
        Router.get().navigate(Routes.VEHICLES);
    }

    /**
     * Fungsi untuk membuka halaman rental
     */
    public void handleGoToRentalView() {
        Router.get().navigate(Routes.RENTALS);
    }

    /**
     * Fungsi untuk membuka halaman kelola admin
     */
    public void handleGoToAdminsView() {
        Router.get().navigate(Routes.ADMINS);
    }

    /**
     * Fungsi untuk membuka halaman login
     */
    public void handleLogin() {
        Router.get().navigate(Routes.LOGIN);
    }

    /**
     * Fungsi logout dan kembali ke halaman kendaraan
     */
    public void handleLogout() {
        Runnable logoutAction = () -> {
            AppContext.get().logout();
            Router.get().navigate(Routes.VEHICLES);
        };
        if (Platform.isFxApplicationThread()) {
            logoutAction.run();
        } else {
            Platform.runLater(logoutAction);
        }
    }

    /**
     * Expose area konten agar Main dapat memberikan area ini ke Router
     */
    public AnchorPane getContentArea() {
        return (sceneContainerInner != null)
                ? sceneContainerInner
                : sceneContainer;
    }
}
