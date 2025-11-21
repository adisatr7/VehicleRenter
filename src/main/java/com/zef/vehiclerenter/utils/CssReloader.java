package com.zef.vehiclerenter.utils;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.Objects;

/**
 * Utility class untuk memuat ulang file CSS styling ketika file-nya diedit tanpa
 * menutup dan membuka kembali aplikasi.
 */
public class CssReloader {

    private static final String[] STYLES = new String[] {
            "/css/style.css"
    };

    /**
     * Aktifkan fitur CSS reload. Tekan [F5] di keyboard untuk melakukan reload.
     *
     * @param scene - Obyek page/scene yang sedang aktif
     */
    public static void enable(Scene scene) {
        scene.addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F5) {
                reload(scene);
            }
        });
    }

    /**
     * Muat ulang page/scene agar style CSS terbaru bisa digunakan
     *
     * @param scene - Obyek page/scene yang sedang aktif
     */
    public static void reload(Scene scene) {
        scene.getStylesheets().clear();
        for (String style : STYLES) {
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            CssReloader.class.getResource(style)).toExternalForm()
            );
        }
        System.out.println("CSS reloaded.");
    }
}
