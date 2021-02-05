/*
 * @(#)AboutAction.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.app;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Window;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.action.AbstractApplicationAction;

import static org.jhotdraw8.app.Application.COPYRIGHT_KEY;
import static org.jhotdraw8.app.Application.LICENSE_KEY;
import static org.jhotdraw8.app.Application.NAME_KEY;
import static org.jhotdraw8.app.Application.VERSION_KEY;

/**
 * Displays a dialog showing information about the application.
 * <p>
 *
 * @author Werner Randelshofer
 */
public class AboutAction extends AbstractApplicationAction {

public static final String ID = "application.about";

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public AboutAction(@NonNull Application app) {
        super(app);
        ApplicationLabels.getResources().configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(@NonNull ActionEvent event, @NonNull Application app) {
        String name = app.get(NAME_KEY);
        String version = app.get(VERSION_KEY);
        String vendor = app.get(COPYRIGHT_KEY);
        String license = app.get(LICENSE_KEY);

        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                (vendor == null ? "" : vendor + "\n")
                        + (license == null ? "" : "" + license + "\n")
                        + (vendor == null && license == null ? "" : "\n")
                        + "Running on"
                        + "\n  Java: " + System.getProperty("java.version")
                        + ", " + System.getProperty("java.vendor")
                        + "\n  JVM: " + System.getProperty("java.vm.version")
                        + ", " + System.getProperty("java.vm.vendor")
                        + "\n  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version")
                        + ", " + System.getProperty("os.arch"));
        alert.getDialogPane().setMaxWidth(640.0);
        alert.setHeaderText((name == null ? "" : name) + (version == null ? "" : (name == null ? "" : " ") + version));
        alert.setGraphic(null);
        if (event.getSource() instanceof Node) {
            Scene scene = ((Node) event.getSource()).getScene();
            Window window = scene==null?null:scene.getWindow();
            alert.initOwner(window);
            alert.initModality(Modality.WINDOW_MODAL);
        }
        alert.getDialogPane().getScene().getStylesheets().addAll(
                getApplication().getStylesheets()
        );
        alert.show();
    }
}
