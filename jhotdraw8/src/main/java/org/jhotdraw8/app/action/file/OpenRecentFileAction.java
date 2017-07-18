/*
 * @(#)OpenRecentFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.CancellationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.DocumentProject;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.net.URIUtil;
import org.jhotdraw8.util.Resources;

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
 * <p>
 * <em>Allow multiple projects per URI</em><br>
 * When the feature is disabled, {@code OpenRecentFileAction} prevents opening
 * an URI which is opened in another view.<br>
 * See {@link org.jhotdraw8.app} for a description of the feature.
 * </p>
 *
 * <p>
 * <em>Open last URI on launch</em><br> {@code OpenRecentFileAction} supplies
 * data for this feature by calling {@link Application#addRecentURI} when it
 * successfully opened a file. See {@link org.jhotdraw8.app} for a description
 * of the feature.
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

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param uri the uri
     */
    public OpenRecentFileAction(Application app, URI uri) {
        super(app);
        this.uri = uri;
        set(Action.LABEL, URIUtil.getName(uri));
    }

    @Override
    protected void handleActionPerformed(ActionEvent evt, Application app) {
        {
            // Search for an empty view
            DocumentProject emptyView;
            if (reuseEmptyViews) {
                emptyView = (DocumentProject) app.getActiveProject();//FIXME class cast exception
                if (emptyView == null
                        || !emptyView.isEmpty()
                        || emptyView.isDisabled()) {
                    emptyView = null;
                }
            } else {
                emptyView = null;
            }

            if (emptyView == null) {
                app.createProject().thenAccept(v -> {
                    app.add(v);
                    doIt((DocumentProject) v, true);
                });
            } else {
                doIt(emptyView, false);
            }
        }
    }

    public void doIt(DocumentProject view, boolean disposeView) {
        openViewFromURI(view, uri);
    }

    private void handleException(final DocumentProject v, Throwable exception) throws MissingResourceException {
        Throwable value = exception;
        exception.printStackTrace();
        String message = (value != null && value.getMessage()
                != null) ? value.getMessage() : value.toString();
        Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
        Alert alert = new Alert(Alert.AlertType.ERROR,
                ((message == null) ? "" : message));
        alert.getDialogPane().setMaxWidth(640.0);
        alert.setHeaderText(labels.getFormatted("file.open.couldntOpen.message", URIUtil.getName(uri)));
        
        // Note: we must invoke clear() or read() on the project, before we start using it.
        v.clear();
        alert.showAndWait();
        v.removeDisabler(this);
    }

    protected void openViewFromURI(final DocumentProject v, final URI uri) {
        final Application app = getApplication();
        v.addDisabler(this);

        DataFormat format = null;
        Map<String, String> query = URIUtil.parseQuery(uri);
        URI u = URIUtil.clearQuery(uri);
        String formatString = query.get("mimeType");
        if (formatString != null) {
            format = DataFormat.lookupMimeType(formatString);
        }
        // Open the file
        try {
            v.read(u, format, null, false).whenComplete((result, exception) -> {
                if (exception instanceof CancellationException) {
                    v.removeDisabler(this);
                } else if (exception != null) {
                    handleException(v, exception);
                } else {
                    v.setURI(uri);
                    v.clearModified();
                    v.setTitle(URIUtil.getName(uri));
                    v.removeDisabler(this);
                }
            });
        } catch (Throwable t) {
            handleException(v, t);
        }
    }
}
