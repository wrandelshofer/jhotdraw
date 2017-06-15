/*
 * @(#)PrintFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.file;

import javafx.event.ActionEvent;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractProjectAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.DocumentProject;

/**
 * Presents a printer chooser to the user and then prints the
 * {@link org.jhotdraw8.app.DocumentProject}.
 * <p>
 * This action requires that the view implements the {@code PrintableView}
 * interface.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PrintFileAction extends AbstractProjectAction<DocumentProject> {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.print";

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public PrintFileAction(Application app) {
        this(app, null);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public PrintFileAction(Application app, DocumentProject view) {
        super(app, view, DocumentProject.class);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, DocumentProject project) {
        project.addDisabler(this);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(project.getNode().getScene().getWindow())) {
            project.print(job).thenRun(() -> project.removeDisabler(this));
        } else {
            Alert alert = new Alert(AlertType.INFORMATION, "Sorry, no printer found");
                alert.getDialogPane().setMaxWidth(640.0);
            alert.show();
            project.removeDisabler(this);
        }
    }
}
