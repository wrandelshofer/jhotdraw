/* @(#)AboutAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.app;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationModel;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.util.Resources;

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
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, Application app) {
        if (app==null) return;

        addDisabler(this);
        ApplicationModel model = app.getModel();

        String javafxVendor = javafx.application.Application.class.getPackage().getImplementationVendor();
        String javafxVersion = javafx.application.Application.class.getPackage().getImplementationVersion();

        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                model.getCopyright()
                + "\n\nRunning on"
                + "\n  Java: " + System.getProperty("java.version")
                + ", " + System.getProperty("java.vendor")
                + "\n  JVM: " + System.getProperty("java.vm.version")
                + ", " + System.getProperty("java.vm.vendor")
                        + "\n  javaFX: " +javafxVersion
                        + ", " + javafxVendor
                + "\n  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version")
                + ", " + System.getProperty("os.arch"));
                alert.getDialogPane().setMaxWidth(640.0);
        alert.setHeaderText(model.getName() + (model.getVersion() == null ? "" : " " + model.getVersion()));
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
