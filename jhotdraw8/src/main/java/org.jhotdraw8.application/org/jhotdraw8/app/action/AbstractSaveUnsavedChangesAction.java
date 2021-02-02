/*
 * @(#)AbstractSaveUnsavedChangesAction.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.DataFormat;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.FileBasedActivity;
import org.jhotdraw8.app.FileBasedApplication;
import org.jhotdraw8.concurrent.SimpleWorkState;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.gui.FileURIChooser;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.util.Resources;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import static org.jhotdraw8.app.action.file.AbstractSaveFileAction.SAVE_CHOOSER_FACTORY_KEY;
import static org.jhotdraw8.app.action.file.AbstractSaveFileAction.SAVE_CHOOSER_KEY;

/**
 * This abstract class can be extended to implement an {@code Action} that asks
 * to write unsaved changes of a {@link FileBasedActivity}
 * before a destructive action is performed.
 * <p>
 * If the view has no unsaved changes, method {@code doIt} is invoked
 * immediately. If unsaved changes are present, a dialog is shown asking whether
 * the user wants to discard the changes, cancel or write the changes before
 * doing it. If the user chooses to discard the changes, {@code doIt} is invoked
 * immediately. If the user chooses to cancel, the action is aborted. If the
 * user chooses to write the changes, the view is saved, and {@code doIt} is
 * only invoked after the view was successfully saved.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractSaveUnsavedChangesAction extends AbstractActivityAction<FileBasedActivity> {



@Nullable
    private Node oldFocusOwner = null;

    /**
     * Creates a new instance.
     *
     * @param activity the view
     */
    public AbstractSaveUnsavedChangesAction(@NonNull FileBasedActivity activity) {
        super(activity);
    }

    @Override
    protected final void onActionPerformed(ActionEvent evt, FileBasedActivity activity) {
        onActionOnViewPerformed(activity);
    }

    @Nullable
    protected URIChooser getChooser(FileBasedActivity view) {
        URIChooser chooser = app.get(SAVE_CHOOSER_KEY);
        if (chooser == null) {
            Supplier<URIChooser> factory = app.get(SAVE_CHOOSER_FACTORY_KEY);
            chooser = factory == null ? new FileURIChooser(FileURIChooser.Mode.SAVE) : factory.get();
            app.set(SAVE_CHOOSER_KEY, chooser);
        }
        return chooser;
    }

    public void onActionOnViewPerformed(@NonNull FileBasedActivity v) {
        if (!v.isDisabled()) {
            final Resources labels = ApplicationLabels.getResources();
            /* Window wAncestor = v.getNode().getScene().getWindow(); */
            oldFocusOwner = getFocusOwner(v.getNode());
            WorkState workState = new SimpleWorkState(getLabel());
            v.addDisabler(workState);
            if (v.isModified()) {
                URI unsavedURI = v.getURI();
                ButtonType[] options = { //
                        new ButtonType(labels.getString("file.saveBefore.saveOption.text"), ButtonBar.ButtonData.YES),//
                        new ButtonType(labels.getString("file.saveBefore.cancelOption.text"), ButtonBar.ButtonData.CANCEL_CLOSE), //
                        new ButtonType(labels.getString("file.saveBefore.dontSaveOption.text"), ButtonBar.ButtonData.NO)//
                };

                final Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        labels.getString("file.saveBefore.doYouWantToSave.details"),
                        options);
                alert.getDialogPane().setMaxWidth(640.0);
                alert.setHeaderText(labels.getFormatted("file.saveBefore.doYouWantToSave.message",//
                        v.getTitle(), v.getDisambiguation()));

                alert.setOnHidden(dialogEvent -> {
                    ButtonType result = alert.getResult();
                    if (result != null) {
                        switch (result.getButtonData()) {
                            default:
                            case CANCEL_CLOSE:
                                v.removeDisabler(workState);
                                if (oldFocusOwner != null) {
                                    oldFocusOwner.requestFocus();
                                }
                                break;
                            case NO:
                                doIt(v).whenComplete((r, e) -> {
                                    // FIXME check success
                                    v.removeDisabler(workState);
                                    if (oldFocusOwner != null) {
                                        oldFocusOwner.requestFocus();
                                    }
                                });
                                break;
                            case YES:
                                // this is a little bit quirky.
                                // saveView may start a worker thread
                                // and thus will enable the view at
                                // a later point in time.
                                saveView(v, workState);
                                break;
                        }
                    }
                });

                Window w = v.getNode().getScene().getWindow();
                if (w instanceof Stage) {
                    ((Stage) w).toFront();
                }
                alert.initOwner(w);
                alert.initModality(Modality.WINDOW_MODAL);
                alert.show();
            } else {

                doIt(v).thenRun(() -> {
                    // FIXME check success
                    v.removeDisabler(workState);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
                });
            }
        }
    }

    @Nullable
    protected Node getFocusOwner(@NonNull Node node) {

        Scene scene = node.getScene();
        return scene == null ? null : scene.getFocusOwner();
    }

    protected void saveView(@NonNull final FileBasedActivity v, WorkState workState) {
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            //int option = fileChooser.showSaveDialog(this);

            URI uri = null;
            Outer:
            while (true) {
                uri = chooser.showDialog(v.getNode());

                // Prevent save to URI that is open in another view!
                // unless multiple views to same URI are supported
                if (uri != null
                        && !app.getNonNull(FileBasedApplication.ALLOW_MULTIPLE_ACTIVITIES_WITH_SAME_URI)) {
                    for (Activity vi : app.getActivities()) {
                        if (vi != v && v.getURI().equals(uri)) {
                            // FIXME Localize message
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You can not save to a file which is already open.");
                            alert.getDialogPane().setMaxWidth(640.0);
                            alert.showAndWait();
                            continue Outer;
                        }
                    }
                }
                break;
            }
            if (uri != null) {
                saveViewToURI(v, uri, chooser, chooser.getDataFormat(), workState);
            }
            v.removeDisabler(workState);
            if (oldFocusOwner != null) {
                oldFocusOwner.requestFocus();
            }
        } else {
            saveViewToURI(v, v.getURI(), null, v.getDataFormat(), workState);
        }
    }

    protected void saveViewToURI(@NonNull final FileBasedActivity v, @NonNull final URI uri, @Nullable final URIChooser chooser, final DataFormat dataFormat, WorkState workState) {
        v.write(uri, chooser == null ? null : dataFormat, Collections.emptyMap(), workState).handle((result, exception) -> {
            if (exception instanceof CancellationException) {
                v.removeDisabler(workState);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else if (exception != null) {
                Throwable value = exception;
                Resources labels = ApplicationLabels.getResources();
                Alert alert = new Alert(Alert.AlertType.ERROR, createErrorMessage(exception));
                alert.getDialogPane().setMaxWidth(640.0);
                alert.setHeaderText(labels.getFormatted("file.save.couldntSave.message", UriUtil.getName(uri)));
                alert.showAndWait();
                v.removeDisabler(workState);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else {
                v.setURI(uri);
                v.clearModified();
                app.getRecentUris().put(uri, dataFormat);
                doIt(v);
            }
            return null;
        });
    }

    protected abstract CompletionStage<Void> doIt(FileBasedActivity p);
}
