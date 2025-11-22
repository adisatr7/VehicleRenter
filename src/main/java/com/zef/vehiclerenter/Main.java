package com.zef.vehiclerenter;

import com.zef.vehiclerenter.core.AppConfig;
import com.zef.vehiclerenter.core.AppContext;
import com.zef.vehiclerenter.core.Router;
import com.zef.vehiclerenter.controllers.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Inisialisasi context / koneksi database
        AppContext.init();

        // Muat layout utama (sidebar + placeholder konten)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RootLayout.fxml"));
        Parent root = loader.load();
        RootLayoutController layoutController = loader.getController();

        Scene scene = new Scene(root, 960, 600);

        stage.setTitle(AppConfig.JUDUL_APLIKASI);
        stage.setMinWidth(AppConfig.LEBAR_RESOLUSI_MINIMAL);
        stage.setMinHeight(AppConfig.TINGGI_RESOLUSI_MINIMAL);
        stage.setScene(scene);
        stage.show();

        // Beri Router hanya area konten di sisi kanan
        Router.init(layoutController.getContentArea());
        Router.get().navigate("vehicles");
    }

    @Override
    public void stop() throws Exception {
        AppContext.shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
