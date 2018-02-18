/* @(#)OpenFileAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import java.util.concurrent.CancellationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.ViewController;
import org.jhotdraw8.app.DocumentOrientedViewModel;

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
     *
     * @param app the application
     */
    public OpenFileAction(Application app) {
        super(app);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }

    protected URIChooser getChooser(DocumentOrientedViewModel view) {
        URIChooser c = app.get(OPEN_CHOOSER_KEY);
        if (c == null) {
            c = getApplication().getModel().createOpenChooser();
            app.set(OPEN_CHOOSER_KEY, c);
        }
        return c;
    }

    @Override
    protected void handleActionPerformed(ActionEvent evt, Application app) {
        {
            app.addDisabler(this);
            // Search for an empty view
            DocumentOrientedViewModel emptyView;
            if (reuseEmptyViews) {
                emptyView = (DocumentOrientedViewModel) app.getActiveView(); // FIXME class cast exception
                if (emptyView == null
                        || !emptyView.isEmpty()
                        || emptyView.isDisabled()) {
                    emptyView = null;
                }
            } else {
                emptyView = null;
            }

            if (emptyView == null) {
                app.createView().thenAccept(v -> doIt((DocumentOrientedViewModel) v, true));
            } else {
                doIt(emptyView, false);
            }
        }
    }

    public void doIt(DocumentOrientedViewModel view, boolean disposeView) {
        URIChooser chooser = getChooser(view);
        URI uri = chooser.showDialog(app.getNode());
        if (uri != null) {
            app.add(view);

            // Prevent same URI from being opened more than once
            if (!getApplication().getModel().isAllowMultipleViewsPerURI()) {
                for (ViewController vp : getApplication().views()) {
                    DocumentOrientedViewModel v = (DocumentOrientedViewModel) vp;
                    if (v.getURI() != null && v.getURI().equals(uri)) {
                        if (disposeView) {
                            app.remove(view);
                        }
                        app.removeDisabler(this);
                        v.getNode().getScene().getWindow().requestFocus();
                        v.getNode().requestFocus();
                        return;
                    }
                }
            }

            openViewFromURI(view, uri, chooser);
        } else {
            if (disposeView) {
                app.remove(view);
            }
            app.removeDisabler(this);
        }
    }

    protected void openViewFromURI(final DocumentOrientedViewModel v, final URI uri, final URIChooser chooser) {
        final Application app = getApplication();
        app.removeDisabler(this);
        v.addDisabler(this);
        final DataFormat dataFormat = chooser.getDataFormat();
        v.setDataFormat(dataFormat);

        // Open the file
        v.read(uri, chooser == null ? null : dataFormat, null, false).whenComplete((result, exception) -> {
            if (exception instanceof CancellationException) {
                v.removeDisabler(this);
            } else if (exception != null) {
                Throwable value = exception;
                value.printStackTrace();
                Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
                Alert alert = new Alert(Alert.AlertType.ERROR, createErrorMessage(value));
                alert.getDialogPane().setMaxWidth(640.0);
                alert.setHeaderText(labels.getFormatted("file.open.couldntOpen.message", UriUtil.getName(uri)));
                alert.showAndWait();
                v.removeDisabler(this);
            } else {

                String mimeType = (dataFormat == null) ? null
                        : dataFormat.getIdentifiers().iterator().next();
                v.setURI(uri);
                v.setDataFormat(dataFormat);
                v.clearModified();
                v.setTitle(UriUtil.getName(uri));
                getApplication().addRecentURI(uri,dataFormat);
                v.removeDisabler(this);
            }
        });
    }
}
