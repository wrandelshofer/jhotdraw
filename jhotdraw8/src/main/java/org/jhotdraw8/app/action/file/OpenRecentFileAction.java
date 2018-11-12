/* @(#)OpenRecentFileAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import java.util.MissingResourceException;
import java.util.concurrent.CancellationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.input.DataFormat;
import javax.annotation.Nonnull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.Labels;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.DocumentOrientedViewController;

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
 * @version $Id$
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
     * @param app the application
     * @param uri the uri
     * @param format the data format that should be used to access the URI
     */
    public OpenRecentFileAction(Application app, @Nonnull URI uri, DataFormat format) {
        super(app);
        this.uri = uri;
        this.format=format;
        set(Action.LABEL, UriUtil.getName(uri));
    }

    @Override
    protected void handleActionPerformed(ActionEvent evt, @Nonnull Application app) {
        {
            // Search for an empty view
            DocumentOrientedViewController emptyView;
            if (reuseEmptyViews) {
                emptyView = (DocumentOrientedViewController) app.getActiveView();//FIXME class cast exception
                if (emptyView == null
                        || !emptyView.isEmpty()
                        || emptyView.isDisabled()) {
                    emptyView = null;
                }
            } else {
                emptyView = null;
            }

            if (emptyView == null) {
                app.createView().thenAccept(v -> {
                    app.add(v);
                    doIt((DocumentOrientedViewController) v, true);
                });
            } else {
                doIt(emptyView, false);
            }
        }
    }

    public void doIt(@Nonnull DocumentOrientedViewController view, boolean disposeView) {
        openViewFromURI(view, uri, format);
    }

    private void handleException(final DocumentOrientedViewController v, Throwable exception) throws MissingResourceException {
        Throwable value = exception;
        exception.printStackTrace();
        Resources labels = Labels.getLabels();
                Alert alert = new Alert(Alert.AlertType.ERROR, createErrorMessage(exception));
        alert.getDialogPane().setMaxWidth(640.0);
        alert.setHeaderText(labels.getFormatted("file.open.couldntOpen.message", UriUtil.getName(uri)));
        
        // Note: we must invoke clear() or read() on the view, before we start using it.
        v.clear();
        alert.showAndWait();
        v.removeDisabler(this);
    }

    protected void openViewFromURI(@Nonnull final DocumentOrientedViewController v, @Nonnull final URI uri, DataFormat format) {
        final Application app = getApplication();
        v.addDisabler(this);

        // Open the file
        try {
            v.read(uri, format, null, false).whenComplete((actualFormat, exception) -> {
                if (exception instanceof CancellationException) {
                    v.removeDisabler(this);
                } else if (exception != null) {
                    handleException(v, exception);
                } else {
                    v.setURI(uri);
                    v.setDataFormat(actualFormat);
                    v.clearModified();
                    v.setTitle(UriUtil.getName(uri));
                    v.removeDisabler(this);
                }
            });
        } catch (Throwable t) {
            handleException(v, t);
        }
    }
}
