/*
 * @(#)OpenRecentFileAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.FileBasedActivity;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.concurrent.SimpleWorkState;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.util.Resources;

import java.net.URI;
import java.util.MissingResourceException;
import java.util.concurrent.CancellationException;

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
 * <em>Allow multiple views per URI</em><br>
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
 */
public class OpenRecentFileAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.openRecent";
    private URI uri;
    private DataFormat format;
    private boolean reuseEmptyViews = true;

    /**
     * Creates a new instance.
     *
     * @param app    the application
     * @param uri    the uri
     * @param format the data format that should be used to access the URI
     */
    public OpenRecentFileAction(Application app, @NonNull URI uri, DataFormat format) {
        super(app);
        this.uri = uri;
        this.format = format;
        set(Action.LABEL, UriUtil.getName(uri));
    }

    @Override
    protected void onActionPerformed(ActionEvent evt, @NonNull Application app) {
        {
            // Search for an empty view
            FileBasedActivity emptyView;
            if (reuseEmptyViews) {
                emptyView = (FileBasedActivity) app.getActiveActivity();//FIXME class cast exception
                if (emptyView == null
                        || !emptyView.isEmpty()
                        || emptyView.isDisabled()) {
                    emptyView = null;
                }
            } else {
                emptyView = null;
            }

            if (emptyView == null) {
                app.createActivity().thenAccept(v -> {
                    app.add(v);
                    doIt((FileBasedActivity) v, true);
                });
            } else {
                doIt(emptyView, false);
            }
        }
    }

    public void doIt(@NonNull FileBasedActivity view, boolean disposeView) {
        openViewFromURI(view, uri, format);
    }

    private void onException(@NonNull final FileBasedActivity v, @NonNull Throwable exception) throws MissingResourceException {
        Throwable value = exception;
        exception.printStackTrace();
        Resources labels = ApplicationLabels.getResources();
        Alert alert = new Alert(Alert.AlertType.ERROR, createErrorMessage(exception));
        alert.getDialogPane().setMaxWidth(640.0);
        alert.setHeaderText(labels.getFormatted("file.open.couldntOpen.message", UriUtil.getName(uri)));

        // Note: we must invoke clear() or read() on the view, before we start using it.
        v.clear();
        alert.showAndWait();
    }

    protected void openViewFromURI(@NonNull final FileBasedActivity v, @NonNull final URI uri, DataFormat format) {
        final Application app = getApplication();
        WorkState workState = new SimpleWorkState(getLabel());
        v.addDisabler(workState);

        // Open the file
        try {
            v.read(uri, format, null, false, workState).whenComplete((actualFormat, exception) -> {
                if (exception instanceof CancellationException) {
                    v.removeDisabler(workState);
                } else if (exception != null) {
                    v.removeDisabler(workState);
                    onException(v, exception);
                } else {
                    v.setURI(uri);
                    v.setDataFormat(actualFormat);
                    v.clearModified();
                    v.setTitle(UriUtil.getName(uri));
                    v.removeDisabler(workState);
                }
            });
        } catch (Throwable t) {
            v.removeDisabler(workState);
            onException(v, t);
        }
    }
}
