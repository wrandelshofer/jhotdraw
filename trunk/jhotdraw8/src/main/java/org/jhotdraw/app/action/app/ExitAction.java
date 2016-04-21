/*
 * @(#)ExitAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.app;

import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractApplicationAction;
import org.jhotdraw.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.Resources;

/**
 * Exits the application after letting the user review and possibly save all
 * unsaved views.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ExitAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "application.exit";
    private Node oldFocusOwner;
    private View unsavedView;

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public ExitAction(Application app) {
        super(app);
        Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(ActionEvent evt) {
        app.addDisabler(this);
        int unsavedViewsCount = 0;
        int disabledViewsCount = 0;
        View documentToBeReviewed = null;
        URI unsavedURI = null;
        for (View p : app.views()) {
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
            app.removeDisabler(this);
            return;
        }

        final Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
        switch (unsavedViewsCount) {
            case 0: {
                doExit();
                break;
            }
            case 1: {
                reviewNext();
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
                            app.removeDisabler(this);
                            break;
                        case NO:
                            app.exit();
                            break;
                        case YES:
                            unsavedView = documentToBeReviewed;
                            reviewChanges();
                            break;
                    }
                } else {
                    app.removeDisabler(this);
                }
            }
        }
    }

    protected URIChooser getChooser(View view) {
        URIChooser chsr = view.get(AbstractSaveUnsavedChangesAction.SAVE_CHOOSER_KEY);
        if (chsr == null) {
            chsr = getApplication().getModel().createSaveChooser();
            view.set(AbstractSaveUnsavedChangesAction.SAVE_CHOOSER_KEY, chsr);
        }
        return chsr;
    }

    protected void saveChanges() {
        View v = unsavedView;
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            URI uri = null;

            Outer:
            while (true) {
                uri = chooser.showDialog(v.getNode());

                // Prevent save to URI that is open in another view!
                // unless  multipe views to same URI are supported
                if (uri != null && !app.getModel().isAllowMultipleViewsPerURI()) {
                    for (View vi : app.views()) {
                        if (vi != v && v.getURI().equals(uri)) {
                            // FIXME Localize message
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You can not save to a file which is already open.");
                            alert.showAndWait();
                            continue Outer;
                        }
                    }
                }
                break;
            }

            if (uri == null) {
                unsavedView.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
                getApplication().removeDisabler(this);
            } else {

                saveToFile(uri, chooser);

            }
        } else {
            saveToFile(v.getURI(), null);
        }
    }

    protected void reviewChanges() {
        if (!unsavedView.isDisabled()) {
            final Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
            oldFocusOwner = unsavedView.getNode().getScene().getFocusOwner();
            unsavedView.removeDisabler(this);
            URI unsavedURI = unsavedView.getURI();
            ButtonType[] options = {
                new ButtonType(labels.getString("application.exit.saveOption.text"), ButtonData.YES),//
                new ButtonType(labels.getString("application.exit.cancelOption.text"), ButtonData.CANCEL_CLOSE),//
                new ButtonType(labels.getString("application.exit.dontSaveOption.text"), ButtonData.NO)//
            };
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    labels.getString("application.exit.doYouWantToSave.details"),
                    options);
            alert.setHeaderText(labels.getFormatted("application.exit.doYouWantToSave.message", //
                    unsavedView.getTitle(), unsavedView.getDisambiguation()));
            unsavedView.getNode().getScene().getWindow().requestFocus();
            alert.initOwner(unsavedView.getNode().getScene().getWindow());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                switch (result.get().getButtonData()) {
                    default:
                    case CANCEL_CLOSE:
                        unsavedView.removeDisabler(this);
                        getApplication().removeDisabler(this);
                        break;
                    case NO:
                        getApplication().remove(unsavedView);
                        unsavedView.removeDisabler(this);
                        reviewNext();
                        break;
                    case YES:
                        saveChangesAndReviewNext();
                        break;
                }
            } else {
                unsavedView.removeDisabler(this);
                getApplication().removeDisabler(this);
            }
        } else {
            getApplication().removeDisabler(this);
        }
    }

    protected void saveChangesAndReviewNext() {
        final View v = unsavedView;
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            URI uri = chooser.showDialog(unsavedView.getNode());
            if (uri != null) {
                saveToFileAndReviewNext(uri, chooser);

            } else {
                v.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
                getApplication().removeDisabler(this);
            }
        } else {
            saveToFileAndReviewNext(v.getURI(), null);
        }
    }

    protected void reviewNext() {
        int unsavedViewsCount = 0;
        View documentToBeReviewed = null;
        for (View p : getApplication().views()) {
            if (p.isModified()) {
                if (!p.isDisabled()) {
                    documentToBeReviewed = p;
                }
                unsavedViewsCount++;
            }
        }
        if (unsavedViewsCount == 0) {
            doExit();
        } else if (documentToBeReviewed != null) {
            unsavedView = documentToBeReviewed;
            reviewChanges();
        } else {
            getApplication().removeDisabler(this);
            //System.out.println("exit silently aborted");
        }
    }

    protected void saveToFile(final URI uri, final URIChooser chooser) {
        final View v = unsavedView;
        v.write(uri).handle((result, exception) -> {
            if (exception instanceof CancellationException) {
                v.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else if (exception != null) {
                Throwable value = exception;
                String message = (value != null && value.getMessage() != null) ? value.getMessage() : value.toString();
                Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        labels.getFormatted("file.save.couldntSave.message", URIUtil.getName(uri)) + "</b><p>"
                        + ((message == null) ? "" : message));
                alert.showAndWait();
                v.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else {
                v.setURI(uri);
                v.clearModified();
                app.addRecentURI(uri);
            }
            return null;
        });
    }

    protected void saveToFileAndReviewNext(final URI uri, final URIChooser chooser) {
        final View v = unsavedView;
        v.write(uri).handle((result, exception) -> {
            if (exception instanceof CancellationException) {
                v.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else if (exception != null) {
                Throwable value = exception.getCause();
                String message = (value != null && value.getMessage() != null) ? value.getMessage() : value.toString();
                Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        labels.getFormatted("file.save.couldntSave.message", URIUtil.getName(uri)) + "</b><p>"
                        + ((message == null) ? "" : message));
                alert.showAndWait();
                v.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else {
                v.setURI(uri);
                v.clearModified();
                reviewNext();
            }
            return null;
        });
    }

    protected void doExit() {
        for (View p : new ArrayList<View>(app.views())) {
            if (!p.isDisabled() && !p.isModified()) {
                app.remove(p);
            }
        }
        if (app.views().isEmpty()) {
            app.exit();
        } else {
            app.removeDisabler(this);
        }
    }
}
