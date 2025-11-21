package com.zef.vehiclerenter.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Router {
    private static Router instance;

    private final StackPane root;
    private final Map<String, String> routes = new HashMap<>();

    private Router(StackPane root) {
        this.root = root;

        // Daftar halaman
        // -> Formatnya: ("nama", "letak file .fxml-nya")
        routes.put("home", "/fxml/HomeView.fxml");
        routes.put("about", "/fxml/AboutView.fxml");

        // routes.put("login", "/fxml/login-view.fxml");
        // routes.put("signup", "/fxml/signup-view.fxml");
        // routes.put("admin/home", "/fxml/admin/HomeView.fxml");
        // routes.put("admin/product", "/fxml/admin/product-view.fxml");
        // routes.put("admin/rents", "/fxml/admin/rents-view.fxml");
    }

    public static void init(StackPane root) {
        if (instance == null) {
            instance = new Router(root);
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
            root.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
