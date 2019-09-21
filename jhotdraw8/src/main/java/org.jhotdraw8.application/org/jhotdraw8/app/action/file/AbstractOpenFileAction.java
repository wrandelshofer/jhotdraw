package org.jhotdraw8.app.action.file;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.DocumentBasedActivity;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.concurrent.SimpleWorkState;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.util.Resources;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CancellationException;


public abstract class AbstractOpenFileAction extends AbstractApplicationAction {
    public final static Key<URIChooser> OPEN_CHOOSER_KEY = new ObjectKey<>("openChooser", URIChooser.class);

    public AbstractOpenFileAction(@Nullable Application app) {
        super(app);
    }

    protected URIChooser getChooser(DocumentBasedActivity view) {
        URIChooser c = app.get(OPEN_CHOOSER_KEY);
        if (c == null) {
            c = getApplication().getModel().createOpenChooser();
            app.set(OPEN_CHOOSER_KEY, c);
        }
        return c;
    }

    protected abstract boolean isReuseEmptyViews();

    @Override
    protected void handleActionPerformed(ActionEvent evt, @Nonnull Application app) {
        {
            WorkState workState = new SimpleWorkState(getLabel());
            app.addDisabler(workState);
            // Search for an empty view
            DocumentBasedActivity emptyView;
            if (isReuseEmptyViews()) {
                emptyView = (DocumentBasedActivity) app.getActiveView(); // FIXME class cast exception
                if (emptyView == null
                        || !emptyView.isEmpty()
                        || emptyView.isDisabled()) {
                    emptyView = null;
                }
            } else {
                emptyView = null;
            }

            if (emptyView == null) {
                app.createView().thenAccept(v -> doIt((DocumentBasedActivity) v, true, workState));
            } else {
                doIt(emptyView, false, workState);
            }
        }
    }


    public void doIt(@Nonnull DocumentBasedActivity view, boolean disposeView, WorkState workState) {
        URIChooser chooser = getChooser(view);
        URI uri = chooser.showDialog(app.getNode());
        if (uri != null) {
            app.add(view);

            // Prevent same URI from being opened more than once
            if (!getApplication().getModel().isAllowMultipleViewsPerURI()) {
                for (Activity vp : getApplication().views()) {
                    DocumentBasedActivity v = (DocumentBasedActivity) vp;
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

    protected void openViewFromURI(@Nonnull final DocumentBasedActivity v, @Nonnull final URI uri, @Nonnull final URIChooser chooser, WorkState workState) {
        final Application app = getApplication();
        Map<? super Key<?>, Object> options = getReadOptions();
        app.removeDisabler(workState);
        if (options == null) {
            return; // The user has decided, that he/she does not want to open a file after all.
        }

        v.addDisabler(workState);
        final DataFormat chosenFormat = chooser.getDataFormat();
        v.setDataFormat(chosenFormat);

        // Open the file
        v.read(uri, chosenFormat, options, false, workState).whenComplete((actualFormat, exception) -> {
            if (exception instanceof CancellationException) {
                v.removeDisabler(workState);
            } else if (exception != null) {
                Throwable value = exception;
                value.printStackTrace();
                Resources labels = ApplicationLabels.getResources();
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
                getApplication().addRecentURI(uri, actualFormat);
                v.removeDisabler(workState);
            }
        });
    }

    /**
     * Gets options for {@link DocumentBasedActivity#read(URI, DataFormat, Map, boolean, WorkState)}.
     * The options can be null, a constant, or from user input through a dialog window.
     * <p>
     * The value null means that the user has aborted the dialog window. In this case, the action
     * will not open a file!
     *
     * @return options or null if the user has aborted the dialog window
     */
    protected abstract Map<? super Key<?>, Object> getReadOptions();

}
