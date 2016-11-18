/*
 * @(#)OpenFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import java.util.concurrent.CancellationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.SimpleKey;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.URIUtil;
import org.jhotdraw8.app.ProjectView;
import org.jhotdraw8.util.Resources;
/**
 * Presents an {@code URIChooser} and loads the selected URI into an
 * empty view. If no empty view is available, a new view is created.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class OpenFileAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    public final static Key<URIChooser> OPEN_CHOOSER_KEY = new SimpleKey<>("openChooser", URIChooser.class);
    public static final String ID = "file.open";
    private boolean reuseEmptyViews = true;

    /** Creates a new instance.
     * @param app the application */
    public OpenFileAction(Application app) {
        super(app);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }

    protected URIChooser getChooser(ProjectView view) {
        URIChooser c = app.get(OPEN_CHOOSER_KEY);
        if (c == null) {
            c = getApplication().getModel().createOpenChooser();
            app.set(OPEN_CHOOSER_KEY, c);
        }
        return c;
    }

    @Override
    protected void onActionPerformed(ActionEvent evt) {
        final Application app = getApplication();
        {
            app.addDisabler(this);
            // Search for an empty view
                      ProjectView emptyView;
            if (reuseEmptyViews) {
                emptyView = app.getActiveView();
                if (emptyView==null
                        || !emptyView.isEmpty()
                        || emptyView.isDisabled()) {
                    emptyView = null;
                }
            } else {
                emptyView = null;
            }

            if (emptyView==null) {
                app.createView().thenAccept(v -> doIt(v, true));
            } else {
                doIt(emptyView, false);
            }
        }
    }

    public void doIt(ProjectView view, boolean disposeView) {
        URIChooser chooser = getChooser(view);
        URI uri = chooser.showDialog(app.getNode());
        if (uri!=null) {
            app.add(view);

            // Prevent same URI from being opened more than once
            if (!getApplication().getModel().isAllowMultipleViewsPerURI()) {
                for (ProjectView v : getApplication().views()) {
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

    protected void openViewFromURI(final ProjectView v, final URI uri, final URIChooser chooser) {
        final Application app = getApplication();
        app.removeDisabler(this);
        v.addDisabler(this);

        // Open the file
        v.read(uri, chooser==null?null:chooser.getDataFormat(),false).whenComplete((result, exception) -> {
            if (exception instanceof CancellationException) {
                    v.removeDisabler(this);
            } else if (exception != null) {
                    Throwable value = exception;
                    value.printStackTrace();
                    String message = (value != null && value.getMessage()
                            != null) ? value.getMessage() : value.toString();
                    Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            ((message == null) ? "" : message));
                    alert.setHeaderText(labels.getFormatted("file.open.couldntOpen.message", URIUtil.getName(uri)));
                    alert.showAndWait();
                    v.removeDisabler(this);
            } else {
                    v.setURI(uri);
                    v.clearModified();
                    v.setTitle(URIUtil.getName(uri));
                    getApplication().addRecentURI(uri);
                    v.removeDisabler(this);
            }
        });
    }
}
