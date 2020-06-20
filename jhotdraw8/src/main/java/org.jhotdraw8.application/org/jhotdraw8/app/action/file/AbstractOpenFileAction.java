/*
 * @(#)AbstractOpenFileAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.FileBasedActivity;
import org.jhotdraw8.app.FileBasedApplication;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.concurrent.SimpleWorkState;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.gui.FileURIChooser;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.util.Resources;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;


public abstract class AbstractOpenFileAction extends AbstractApplicationAction {
    @NonNull
    public final static Key<URIChooser> OPEN_CHOOSER_KEY = new ObjectKey<>("openChooser", URIChooser.class);
    @NonNull
    public final static Key<Supplier<URIChooser>> OPEN_CHOOSER_FACTORY_KEY = new ObjectKey<>("openChooserFactory", Supplier.class, new Class<?>[]{URIChooser.class}, null);

    public AbstractOpenFileAction(@NonNull FileBasedApplication app) {
        super(app);
    }

    @Nullable
    protected URIChooser getChooser(FileBasedActivity view) {
        URIChooser chooser = app.get(OPEN_CHOOSER_KEY);
        if (chooser == null) {
            Supplier<URIChooser> factory = app.get(OPEN_CHOOSER_FACTORY_KEY);
            chooser = factory == null ? new FileURIChooser() : factory.get();
            app.put(OPEN_CHOOSER_KEY, chooser);
        }
        return chooser;
    }

    protected abstract boolean isReuseEmptyViews();

    @Override
    protected void onActionPerformed(@NonNull ActionEvent evt, @NonNull Application app) {
        {
            WorkState workState = new SimpleWorkState(getLabel());
            app.addDisabler(workState);
            // Search for an empty view
            FileBasedActivity emptyView;
            if (isReuseEmptyViews()) {
                emptyView = (FileBasedActivity) app.getActiveActivity(); // FIXME class cast exception
                if (emptyView == null
                        || !emptyView.isEmpty()
                        || emptyView.isDisabled()) {
                    emptyView = null;
                }
            } else {
                emptyView = null;
            }

            if (emptyView == null) {
                app.createActivity().thenAccept(v -> doIt((FileBasedActivity) v, true, workState));
            } else {
                doIt(emptyView, false, workState);
            }
        }
    }


    public void doIt(@NonNull FileBasedActivity view, boolean disposeView, WorkState workState) {
        URIChooser chooser = getChooser(view);
        URI uri = chooser.showDialog(app.getNode());
        if (uri != null) {
            app.add(view);

            // Prevent same URI from being opened more than once
            if (!getApplication().getNonNull(FileBasedApplication.ALLOW_MULTIPLE_ACTIVITIES_WITH_SAME_URI)) {
                for (Activity vp : getApplication().getActivities()) {
                    FileBasedActivity v = (FileBasedActivity) vp;
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

    protected void openViewFromURI(@NonNull final FileBasedActivity v, @NonNull final URI uri, @NonNull final URIChooser chooser, WorkState workState) {
        final Application app = getApplication();
        Map<? super Key<?>, Object> options = getReadOptions();
        if (app != null) {
            app.removeDisabler(workState);
        }
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
                exception.printStackTrace();
                Resources labels = ApplicationLabels.getResources();

                TextArea textArea = new TextArea(createErrorMessage(exception));
                textArea.setEditable(false);
                textArea.setWrapText(true);

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.getDialogPane().setMaxWidth(640.0);
                alert.setHeaderText(labels.getFormatted("file.open.couldntOpen.message", UriUtil.getName(uri)));
                alert.getDialogPane().setContent(textArea);
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
     * Gets options for {@link FileBasedActivity#read(URI, DataFormat, Map, boolean, WorkState)}.
     * The options can be null, a constant, or from user input through a dialog window.
     * <p>
     * The value null means that the user has aborted the dialog window. In this case, the action
     * will not open a file!
     *
     * @return options or null if the user has aborted the dialog window
     */
    @NonNull
    protected abstract Map<? super Key<?>, Object> getReadOptions();


}
