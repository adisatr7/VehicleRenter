package com.zef.vehiclerenter.core;

import com.zef.vehiclerenter.routes.Routes;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Router {
    private static Router instance;

    private final Pane contentRoot;
    private final Map<String, String> routes = new HashMap<>();

    private Router(Pane contentRoot) {
        this.contentRoot = contentRoot;

        // Daftar halaman
        routes.put(Routes.VEHICLES, "/fxml/VehiclesView.fxml");
        routes.put(Routes.RENTALS, "/fxml/RentalsView.fxml");
        routes.put(Routes.LOGIN, "/fxml/LoginView.fxml");
    }

    public static void init(Pane contentRoot) {
        if (instance == null) {
            instance = new Router(contentRoot);
        }
    }

    public static Router get() {
        if (instance == null) {
            throw new IllegalStateException("Router belum berjalan");
        }
        return instance;
    }

    /**
     * Method untuk pindah halaman.
     *
     * @param name - Nama halaman yang dituju
     */
    public void navigate(String name) {
        String fxmlPath = routes.get(name);
        if (fxmlPath == null) {
            throw new IllegalArgumentException("Route tidak dikenal: " + name);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Jika contentRoot adalah AnchorPane, set anchor agar view mengisi penuh (w-full h-full)
            if (contentRoot instanceof AnchorPane) {
                AnchorPane.setTopAnchor(view, 0.0);
                AnchorPane.setBottomAnchor(view, 0.0);
                AnchorPane.setLeftAnchor(view, 0.0);
                AnchorPane.setRightAnchor(view, 0.0);
            }

            contentRoot.getChildren().setAll(view);  // hanya ganti panel kanan
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
