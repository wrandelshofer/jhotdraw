/* @(#)PrintFileAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import javafx.event.ActionEvent;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.DocumentOrientedActivity;
import org.jhotdraw8.app.Labels;
import org.jhotdraw8.app.action.AbstractViewControllerAction;
import org.jhotdraw8.concurrent.WorkState;

/**
 * Presents a printer chooser to the user and then prints the
 * {@link DocumentOrientedActivity}.
 * <p>
 * This action requires that the view implements the {@code PrintableView}
 * interface.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PrintFileAction extends AbstractViewControllerAction<DocumentOrientedActivity> {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.print";

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public PrintFileAction( Application app) {
        this(app, null);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public PrintFileAction( Application app, @Nullable DocumentOrientedActivity view) {
        super(app, view, DocumentOrientedActivity.class);
        Labels.getLabels().configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, @Nonnull DocumentOrientedActivity view) {
        WorkState workState = new WorkState();
        view.addDisabler(workState);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(view.getNode().getScene().getWindow())) {
            view.print(job, workState).thenRun(() -> view.removeDisabler(workState));
        } else {
            Alert alert = new Alert(AlertType.INFORMATION, "Sorry, no printer found");
                alert.getDialogPane().setMaxWidth(640.0);
            alert.show();
            view.removeDisabler(workState);
        }
    }
}
