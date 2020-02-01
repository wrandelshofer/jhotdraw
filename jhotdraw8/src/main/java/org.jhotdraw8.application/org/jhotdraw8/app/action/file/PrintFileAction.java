/*
 * @(#)PrintFileAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import javafx.event.ActionEvent;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.DocumentBasedActivity;
import org.jhotdraw8.app.action.AbstractActivityAction;
import org.jhotdraw8.concurrent.SimpleWorkState;
import org.jhotdraw8.concurrent.WorkState;

/**
 * Presents a printer chooser to the user and then prints the
 * {@link DocumentBasedActivity}.
 * <p>
 * This action requires that the view implements the {@code PrintableView}
 * interface.
 *
 * @author Werner Randelshofer
 */
public class PrintFileAction extends AbstractActivityAction<DocumentBasedActivity> {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.print";

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public PrintFileAction(@NonNull Application app) {
        this(app, null);
    }

    /**
     * Creates a new instance.
     *
     * @param app  the application
     * @param view the view
     */
    public PrintFileAction(@NonNull Application app, @Nullable DocumentBasedActivity view) {
        super(app, view, DocumentBasedActivity.class);
        ApplicationLabels.getResources().configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(ActionEvent event, @NonNull DocumentBasedActivity activity) {
        WorkState workState = new SimpleWorkState();
        activity.addDisabler(workState);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(activity.getNode().getScene().getWindow())) {
            activity.print(job, workState).thenRun(() -> activity.removeDisabler(workState));
        } else {
            Alert alert = new Alert(AlertType.INFORMATION, "Sorry, no printer found");
            alert.getDialogPane().setMaxWidth(640.0);
            alert.show();
            activity.removeDisabler(workState);
        }
    }
}
