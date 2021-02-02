/*
 * @(#)FxmlUtil.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.fxml;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class FxmlUtil {
    private FxmlUtil() {
        // prevent instantiation
    }

    public static @NonNull <T> Supplier<T> createFxmlControllerSupplier(@NonNull URL fxml,
                                                                        @NonNull ResourceBundle resources) {
        return createFxmlControllerSupplier(fxml, resources, (Callback<Class<?>, Object>) null);
    }


    public static @NonNull <T> Supplier<T> createFxmlControllerSupplier(@NonNull URL fxml,
                                                                        @NonNull ResourceBundle resources,
                                                                        @Nullable Supplier<T> controllerFactory) {
        return () -> FxmlUtil.<T>createFxmlControllerSupplier(fxml, resources, controllerFactory == null ? null : clazz -> controllerFactory.get()).get();
    }

    public static @NonNull <T> Supplier<T> createFxmlControllerSupplier(@NonNull URL fxml,
                                                                        @NonNull ResourceBundle resources,
                                                                        @Nullable Callback<Class<?>, Object> controllerFactory) {
        return () -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setControllerFactory(controllerFactory);
            try (InputStream in = fxml.openStream()) {
                loader.load(in);
                return loader.getController();
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        };
    }

    public static @NonNull <T extends Node> Supplier<T> createFxmlNodeSupplier(@NonNull URL fxml, ResourceBundle resourceBundle) {
        return () -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resourceBundle);
            try (InputStream in = fxml.openStream()) {
                return loader.load(in);
            } catch (IOException ex) {
                throw new InternalError(ex);
            }
        };
    }
}
