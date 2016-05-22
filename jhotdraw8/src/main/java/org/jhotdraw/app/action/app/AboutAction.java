/*
 * @(#)AboutAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.app;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.ApplicationModel;
import org.jhotdraw.app.action.AbstractApplicationAction;
import org.jhotdraw.util.Resources;

/**
 * Displays a dialog showing information about the application.
 * <p>
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class AboutAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "application.about";

    /** Creates a new instance.
     * @param app the application */
    public AboutAction(Application app) {
        super(app);
        Resources.getResources("org.jhotdraw.app.Labels").configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(ActionEvent evt) {
        addDisabler(this);
        ApplicationModel model = getApplication().getModel();

        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                model.getCopyright()
                + "\n\nRunning on"
                + "\n  Java: " + System.getProperty("java.version")
                + ", " + System.getProperty("java.vendor")
                + "\n  JVM: " + System.getProperty("java.vm.version")
                + ", " + System.getProperty("java.vm.vendor")
                + "\n  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version")
                + ", " + System.getProperty("os.arch"));
        alert.setHeaderText(model.getName() + (model.getVersion() == null ? "" : " " + model.getVersion()));
        alert.setGraphic(null);
        alert.initModality(Modality.NONE);
        alert.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == false) {
                removeDisabler(this);
            }
        }
        );
        alert.show();
    }
}
