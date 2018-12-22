/* @(#)OpenFileAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import java.util.concurrent.CancellationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.DocumentOrientedActivity;
import org.jhotdraw8.app.Labels;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Activity;

/**
 * Presents an {@code URIChooser} and loads the selected URI into an empty view.
 * If no empty view is available, a new view is created.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class OpenFileAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    public final static Key<URIChooser> OPEN_CHOOSER_KEY = new ObjectKey<>("openChooser", URIChooser.class);
    public static final String ID = "file.open";
    private boolean reuseEmptyViews = true;

    /**
     * Creates a new instance.
     *v
     * @param app the application
     */
    public OpenFileAction(Application app) {
        super(app);
        Labels.getLabels().configureAction(this, ID);
    }

    protected URIChooser getChooser(DocumentOrientedActivity view) {
        URIChooser c = app.get(OPEN_CHOOSER_KEY);
        if (c == null) {
            c = getApplication().getModel().createOpenChooser();
            app.set(OPEN_CHOOSER_KEY, c);
        }
        return c;
    }

    @Override
    protected void handleActionPerformed(ActionEvent evt, @Nonnull Application app) {
        {
            WorkState workState = new WorkState(getLabel());
            app.addDisabler(workState);
            // Search for an empty view
            DocumentOrientedActivity emptyView;
            if (reuseEmptyViews) {
                emptyView = (DocumentOrientedActivity) app.getActiveView(); // FIXME class cast exception
                if (emptyView == null
                        || !emptyView.isEmpty()
                        || emptyView.isDisabled()) {
                    emptyView = null;
                }
            } else {
                emptyView = null;
            }

            if (emptyView == null) {
                app.createView().thenAccept(v -> doIt((DocumentOrientedActivity) v, true, workState));
            } else {
                doIt(emptyView, false, workState);
            }
        }
    }

    public void doIt(@Nonnull DocumentOrientedActivity view, boolean disposeView, WorkState workState) {
        URIChooser chooser = getChooser(view);
        URI uri = chooser.showDialog(app.getNode());
        if (uri != null) {
            app.add(view);

            // Prevent same URI from being opened more than once
            if (!getApplication().getModel().isAllowMultipleViewsPerURI()) {
                for (Activity vp : getApplication().views()) {
                    DocumentOrientedActivity v = (DocumentOrientedActivity) vp;
                    if (v.getURI() != null && v.getURI().equals(uri)) {
                        if (disposeView) {
                            app.remove(view);
                        }
                        app.removeDisabler(workState);
                        v.getNode().getScene().getWindow().requestFocus();
                        v.getNode().requestFocus();
                        return;
                    }
                }
            }

            openViewFromURI(view, uri, chooser, workState);
        } else {
            if (disposeView) {
                app.remove(view);
            }
            app.removeDisabler(workState);
        }
    }

    protected void openViewFromURI(@Nonnull final DocumentOrientedActivity v, @Nonnull final URI uri, @Nonnull final URIChooser chooser, WorkState workState) {
        final Application app = getApplication();
        app.removeDisabler(workState);
        v.addDisabler(workState);
        final DataFormat chosenFormat = chooser.getDataFormat();
        v.setDataFormat(chosenFormat);

        // Open the file
        v.read(uri, chooser == null ? null : chosenFormat, null, false, workState).whenComplete((actualFormat, exception) -> {
            if (exception instanceof CancellationException) {
                v.removeDisabler(workState);
            } else if (exception != null) {
                Throwable value = exception;
                value.printStackTrace();
                Resources labels = Labels.getLabels();
                Alert alert = new Alert(Alert.AlertType.ERROR, createErrorMessage(value));
                alert.getDialogPane().setMaxWidth(640.0);
                alert.setHeaderText(labels.getFormatted("file.open.couldntOpen.message", UriUtil.getName(uri)));
                alert.showAndWait();
                v.removeDisabler(workState);
            } else {

                String mimeType = (actualFormat == null) ? null
                        : actualFormat.getIdentifiers().iterator().next();
                v.setURI(uri);
                v.setDataFormat(actualFormat);
                v.clearModified();
                v.setTitle(UriUtil.getName(uri));
                getApplication().addRecentURI(uri,actualFormat);
                v.removeDisabler(workState);
            }
        });
    }
}
