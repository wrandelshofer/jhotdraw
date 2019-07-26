/*
 * @(#)AboutAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.app;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.ApplicationModel;
import org.jhotdraw8.app.action.AbstractApplicationAction;

/**
 * Displays a dialog showing information about the application.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AboutAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "application.about";

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public AboutAction(Application app) {
        super(app);
        ApplicationLabels.getResources().configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, Application app) {
        if (app == null) {
            return;
        }

        addDisabler(this);
        ApplicationModel model = app.getModel();

        String name = model.getName();
        String version = model.getVersion();
        String vendor = model.getVendor();
        String license = model.getLicense();



        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                (vendor == null ? "" : vendor)
                        + (license == null ? "" : "\n" + license)
                        + "\n\nRunning on"
                        + "\n  Java: " + System.getProperty("java.version")
                        + ", " + System.getProperty("java.vendor")
                        + "\n  JVM: " + System.getProperty("java.vm.version")
                        + ", " + System.getProperty("java.vm.vendor")
                        + "\n  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version")
                        + ", " + System.getProperty("os.arch"));
        alert.getDialogPane().setMaxWidth(640.0);
        alert.setHeaderText((name == null ? "unnamed" : name) + (version == null ? "" : " " + version));
        alert.setGraphic(null);
        alert.initModality(Modality.NONE);
        alert.showingProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue) {
                        removeDisabler(this);
                    }
                }
        );
        alert.show();
    }
}
