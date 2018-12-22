/* @(#)ExitAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.app;

import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.DocumentOrientedActivity;
import org.jhotdraw8.app.Labels;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.util.Resources;

/**
 * Exits the application after letting the user review and possibly save all
 unsaved views.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ExitAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "application.exit";
    private Node oldFocusOwner;
    @Nullable
    private DocumentOrientedActivity unsavedView;

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public ExitAction(Application app) {
        super(app);
        Labels.getLabels().configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, @Nonnull Application app) {

        WorkState workState=new WorkState(getLabel());
        app.addDisabler(workState);
        int unsavedViewsCount = 0;
        int disabledViewsCount = 0;
        DocumentOrientedActivity documentToBeReviewed = null;
        URI unsavedURI = null;
        for (Activity pr : app.views()) {
            DocumentOrientedActivity p =(DocumentOrientedActivity)pr;
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

        final Resources labels = Labels.getLabels();
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

    protected URIChooser getChooser(@Nonnull DocumentOrientedActivity view) {
        URIChooser chsr = view.get(AbstractSaveUnsavedChangesAction.SAVE_CHOOSER_KEY);
        if (chsr == null) {
            chsr = getApplication().getModel().createSaveChooser();
            view.set(AbstractSaveUnsavedChangesAction.SAVE_CHOOSER_KEY, chsr);
        }
        return chsr;
    }

    protected void saveChanges(WorkState workState) {
        DocumentOrientedActivity v = unsavedView;
        Resources labels=Labels.getLabels();
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            URI uri = null;

            Outer:
            while (true) {
                uri = chooser.showDialog(v.getNode());

                // Prevent save to URI that is open in another view!
                // unless  multipe views to same URI are supported
                if (uri != null && !app.getModel().isAllowMultipleViewsPerURI()) {
                    for (Activity p : app.views()) {
                        DocumentOrientedActivity vi = (DocumentOrientedActivity)p;
                        if (vi != v && v.getURI().equals(uri)) {
                            // FIXME Localize message
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, labels.getString("application.exit.canNotSaveToOpenFile"));
                            alert.getDialogPane().setMaxWidth(640.0);
                            alert.showAndWait();
                            continue Outer;
                        }
                    }
                }
                break;
            }

            if (uri == null) {
                unsavedView.removeDisabler(workState);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
                getApplication().removeDisabler(workState);
            } else {

                saveToFile(uri, chooser.getDataFormat(), workState);

            }
        } else {
            saveToFile(v.getURI(), null, workState);
        }
    }

    protected void reviewChanges(WorkState workState) {
        if (!unsavedView.isDisabled()) {
            final Resources labels = Labels.getLabels();
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
                        getApplication().remove(unsavedView);
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
        final DocumentOrientedActivity v = unsavedView;
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
        DocumentOrientedActivity documentToBeReviewed = null;
        for (Activity pr : getApplication().views()) {
            DocumentOrientedActivity p=(DocumentOrientedActivity)pr;
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

    protected void saveToFile(@Nonnull final URI uri, final DataFormat format, WorkState workState) {
        final DocumentOrientedActivity v = unsavedView;
        v.write(uri, format,null, new WorkState()).handle((result, exception) -> {
            if (exception instanceof CancellationException) {
                v.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else if (exception != null) {
                Throwable value = exception;
                String message = (value != null && value.getMessage() != null) ? value.getMessage() : value.toString();
                Resources labels = Labels.getLabels();
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
                app.addRecentURI(uri,format);
            }
            return null;
        });
    }

    protected void saveToFileAndReviewNext(@Nonnull final URI uri, final DataFormat format, WorkState workState) {
        final DocumentOrientedActivity v = unsavedView;
        v.write(uri, format,null, new WorkState()).handle((result, exception) -> {
            if (exception instanceof CancellationException) {
                v.removeDisabler(workState);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else if (exception != null) {
                Throwable value = exception.getCause();
                String message = (value != null && value.getMessage() != null) ? value.getMessage() : value.toString();
                Resources labels = Labels.getLabels();
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
        for (Activity pr : new ArrayList<>(app.views())) {
            DocumentOrientedActivity p=(DocumentOrientedActivity)pr;
            if (!p.isDisabled() && !p.isModified()) {
                app.remove(p);
            }
        }
        if (app.views().isEmpty()) {
            app.exit();
        } else {
            app.removeDisabler(workState);
        }
    }
}
