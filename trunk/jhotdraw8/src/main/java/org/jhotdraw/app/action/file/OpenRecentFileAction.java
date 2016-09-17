/*
 * @(#)OpenRecentFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.net.URI;
import java.util.concurrent.CancellationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.action.AbstractApplicationAction;
import org.jhotdraw.app.action.Action;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.Resources;
import org.jhotdraw.app.ProjectView;

/**
 * Loads the specified URI into an empty view. If no empty view is available, a
 * new view is created.
 * <p>
 * This action is called when the user selects an item in the Recent Files
 * submenu of the File menu. The action and the menu item is automatically
 * created by the application, when the {@code ApplicationModel} provides a
 * {@code OpenFileAction}.
 * <hr>
 * <b>Features</b>
 *
 * <p><em>Allow multiple views per URI</em><br>
 * When the feature is disabled, {@code OpenRecentFileAction} prevents opening
 * an URI which is opened in another view.<br>
 * See {@link org.jhotdraw.app} for a description of the feature.
 * </p>
 *
 * <p><em>Open last URI on launch</em><br>
 * {@code OpenRecentFileAction} supplies data for this feature by calling
 * {@link Application#addRecentURI} when it successfully opened a file.
 * See {@link org.jhotdraw.app} for a description of the feature.
 * </p>
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class OpenRecentFileAction extends AbstractApplicationAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "file.openRecent";
    private URI uri;
    private boolean reuseEmptyViews = true;

    /** Creates a new instance.
     * @param app the application
     * @param uri the uri */
    public OpenRecentFileAction(Application app, URI uri) {
        super(app);
        this.uri = uri;
        set(Action.LABEL, URIUtil.getName(uri));
    }
    @Override
    protected void onActionPerformed(ActionEvent evt) {
        final Application app = getApplication();
        {
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
                app.createView().thenAccept(v -> {app.add(v);doIt(v, true);});
            } else {
                doIt(emptyView, false);
            }
        }
    }

    public void doIt(ProjectView view, boolean disposeView) {
            openViewFromURI(view, uri);
    }

    protected void openViewFromURI(final ProjectView v, final URI uri) {
        final Application app = getApplication();
        v.addDisabler(this);

        // Open the file
        v.read(uri, false).whenComplete((result, exception) -> {
            if (exception instanceof CancellationException) {
                    v.removeDisabler(this);
            } else if (exception != null) {
                    Throwable value = exception;
                    exception.printStackTrace();
                    String message = (value != null && value.getMessage()
                            != null) ? value.getMessage() : value.toString();
                    Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            ((message == null) ? "" : message));
                    alert.setHeaderText(labels.getFormatted("file.open.couldntOpen.message", URIUtil.getName(uri)));
                    alert.showAndWait();
                    v.removeDisabler(this);
            } else {
                    v.setURI(uri);
                    v.clearModified();
                    v.setTitle(URIUtil.getName(uri));
                    v.removeDisabler(this);
            }
        });
    }
}
