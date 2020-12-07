/*
 * @(#)ExitAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.app;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.FileBasedActivity;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.concurrent.SimpleWorkState;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.gui.FileURIChooser;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.util.Resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

import static org.jhotdraw8.app.action.file.AbstractSaveFileAction.SAVE_CHOOSER_FACTORY_KEY;
import static org.jhotdraw8.app.action.file.AbstractSaveFileAction.SAVE_CHOOSER_KEY;

/**
 * Exits the application after letting the user review and possibly save all
 * unsaved views.
 * <p>
 *
 * @author Werner Randelshofer
 */
public class ExitAction extends AbstractApplicationAction {

public static final String ID = "application.exit";
    private Node oldFocusOwner;
    @Nullable
    private FileBasedActivity unsavedView;

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public ExitAction(Application app) {
        super(app);
        ApplicationLabels.getResources().configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(@NonNull ActionEvent event, @NonNull Application app) {

        WorkState workState = new SimpleWorkState(getLabel());
        app.addDisabler(workState);
        int unsavedViewsCount = 0;
        int disabledViewsCount = 0;
        FileBasedActivity documentToBeReviewed = null;
        URI unsavedURI = null;
        for (Activity pr : app.getActivities()) {
            FileBasedActivity p = (FileBasedActivity) pr;
            if (p.isDisabled()) {
                disabledViewsCount++;
            }
            if (p.isModified()) {
                if (!p.isDisabled()) {
                    documentToBeReviewed = p;
                }
                unsavedURI = p.getURI();
                unsavedViewsCount++;
            }
        }
        if (unsavedViewsCount > 0 && documentToBeReviewed == null) {
            // Silently abort, if no view can be reviewed.
            app.removeDisabler(workState);
            return;
        }

        final Resources labels = ApplicationLabels.getResources();
        switch (unsavedViewsCount) {
            case 0: {
                doExit(workState);
                break;
            }
            case 1: {
                reviewNext(workState);
                break;
            }
            default: {
                ButtonType[] options = { //
                        new ButtonType(labels.getString("application.exit.reviewChangesOption.text"), ButtonBar.ButtonData.YES),//
                        new ButtonType(labels.getString("application.exit.cancelOption.text"), ButtonBar.ButtonData.CANCEL_CLOSE), //
                        new ButtonType(labels.getString("application.exit.discardChangesOption.text"), ButtonBar.ButtonData.NO)//
                };
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,//
                        labels.getString("application.exit.doYouWantToReview.details"),
                        options);
                alert.setHeaderText(labels.getFormatted("application.exit.doYouWantToReview.message", unsavedViewsCount));
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    switch (result.get().getButtonData()) {
                        default:
                        case CANCEL_CLOSE:
                            app.removeDisabler(workState);
                            break;
                        case NO:
                            app.exit();
                            break;
                        case YES:
                            unsavedView = documentToBeReviewed;
                            reviewChanges(workState);
                            break;
                    }
                } else {
                    app.removeDisabler(workState);
                }
            }
        }
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

    protected void reviewChanges(WorkState workState) {
        if (!unsavedView.isDisabled()) {
            final Resources labels = ApplicationLabels.getResources();
            oldFocusOwner = unsavedView.getNode().getScene().getFocusOwner();
            unsavedView.removeDisabler(workState);
            URI unsavedURI = unsavedView.getURI();
            ButtonType[] options = {
                    new ButtonType(labels.getString("application.exit.saveOption.text"), ButtonData.YES),//
                    new ButtonType(labels.getString("application.exit.cancelOption.text"), ButtonData.CANCEL_CLOSE),//
                    new ButtonType(labels.getString("application.exit.dontSaveOption.text"), ButtonData.NO)//
            };
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    labels.getString("application.exit.doYouWantToSave.details"),
                    options);
            alert.getDialogPane().setMaxWidth(640.0);
            alert.setHeaderText(labels.getFormatted("application.exit.doYouWantToSave.message", //
                    unsavedView.getTitle(), unsavedView.getDisambiguation()));
            unsavedView.getNode().getScene().getWindow().requestFocus();
            alert.initOwner(unsavedView.getNode().getScene().getWindow());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                switch (result.get().getButtonData()) {
                    default:
                    case CANCEL_CLOSE:
                        unsavedView.removeDisabler(workState);
                        getApplication().removeDisabler(workState);
                        break;
                    case NO:
                        getApplication().getActivities().remove(unsavedView);
                        unsavedView.removeDisabler(workState);
                        reviewNext(workState);
                        break;
                    case YES:
                        saveChangesAndReviewNext(workState);
                        break;
                }
            } else {
                unsavedView.removeDisabler(workState);
                getApplication().removeDisabler(workState);
            }
        } else {
            getApplication().removeDisabler(workState);
        }
    }

    protected void saveChangesAndReviewNext(WorkState workState) {
        final FileBasedActivity v = unsavedView;
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            URI uri = chooser.showDialog(unsavedView.getNode());
            if (uri != null) {
                saveToFileAndReviewNext(uri, chooser.getDataFormat(), workState);

            } else {
                v.removeDisabler(workState);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
                getApplication().removeDisabler(workState);
            }
        } else {
            saveToFileAndReviewNext(v.getURI(), v.getDataFormat(), workState);
        }
    }

    protected void reviewNext(WorkState workState) {
        int unsavedViewsCount = 0;
        FileBasedActivity documentToBeReviewed = null;
        for (Activity pr : getApplication().getActivities()) {
            FileBasedActivity p = (FileBasedActivity) pr;
            if (p.isModified()) {
                if (!p.isDisabled()) {
                    documentToBeReviewed = p;
                }
                unsavedViewsCount++;
            }
        }
        if (unsavedViewsCount == 0) {
            doExit(workState);
        } else if (documentToBeReviewed != null) {
            unsavedView = documentToBeReviewed;
            reviewChanges(workState);
        } else {
            getApplication().removeDisabler(workState);
            //System.out.println("exit silently aborted");
        }
    }

    protected void saveToFile(@NonNull final URI uri, final DataFormat format, WorkState workState) {
        final FileBasedActivity v = unsavedView;
        v.write(uri, format, Collections.emptyMap(), workState).handle((result, exception) -> {
            if (exception instanceof CancellationException) {
                v.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else if (exception != null) {
                Throwable value = exception;
                String message = (value != null && value.getMessage() != null) ? value.getMessage() : value.toString();
                Resources labels = ApplicationLabels.getResources();
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        labels.getFormatted("file.save.couldntSave.message", UriUtil.getName(uri)) + "</b><p>"
                                + ((message == null) ? "" : message));
                alert.getDialogPane().setMaxWidth(640.0);
                alert.showAndWait();
                v.removeDisabler(workState);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else {
                v.setURI(uri);
                v.clearModified();
                app.getRecentUris().put(uri, format);
            }
            return null;
        });
    }

    protected void saveToFileAndReviewNext(@NonNull final URI uri, final DataFormat format, WorkState workState) {
        final FileBasedActivity v = unsavedView;
        v.write(uri, format, Collections.emptyMap(), workState).handle((result, exception) -> {
            if (exception instanceof CancellationException) {
                v.removeDisabler(workState);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else if (exception != null) {
                Throwable value = exception.getCause();
                String message = (value != null && value.getMessage() != null) ? value.getMessage() : value.toString();
                Resources labels = ApplicationLabels.getResources();
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        labels.getFormatted("file.save.couldntSave.message", UriUtil.getName(uri)) + "</b><p>"
                                + ((message == null) ? "" : message));
                alert.getDialogPane().setMaxWidth(640.0);
                alert.showAndWait();
                v.removeDisabler(workState);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else {
                v.setURI(uri);
                v.clearModified();
                reviewNext(workState);
            }
            return null;
        });
    }

    protected void doExit(WorkState workState) {
        for (Activity pr : new ArrayList<>(app.getActivities())) {
            FileBasedActivity p = (FileBasedActivity) pr;
            if (!p.isDisabled() && !p.isModified()) {
                app.getActivities().remove(p);
            }
        }
        if (app.getActivities().isEmpty()) {
            app.exit();
        } else {
            app.removeDisabler(workState);
        }
    }
}
