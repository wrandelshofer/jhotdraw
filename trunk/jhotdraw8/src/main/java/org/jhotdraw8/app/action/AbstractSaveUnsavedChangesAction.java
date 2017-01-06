/*
 * @(#)AbstractSaveUnsavedChangesAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app.action;

import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionStage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.SimpleKey;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.URIUtil;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Project;
import org.jhotdraw8.app.DocumentProject;

/**
 * This abstract class can be extended to implement an {@code Action} that asks
 * to write unsaved changes of a {@link org.jhotdraw8.app.ProjectView} before a
 * destructive action is performed.
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
 * @version $Id: AbstractSaveUnsavedChangesAction.java 788 2014-03-22 07:56:28Z
 * rawcoder $
 */
public abstract class AbstractSaveUnsavedChangesAction extends AbstractProjectAction<DocumentProject> {

    /**
     *
     */
    public final static Key<URIChooser> SAVE_CHOOSER_KEY = new SimpleKey<URIChooser>(
            "saveChooser", URIChooser.class, null);

    private static final long serialVersionUID = 1L;

    private Node oldFocusOwner = null;

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public AbstractSaveUnsavedChangesAction(Application app, DocumentProject view) {
        super(app, view,DocumentProject.class);
    }

    @Override
    protected final void handleActionPerformed(ActionEvent evt, DocumentProject av) {
        Application app = getApplication();
        if (av instanceof DocumentProject) {
            handleActionOnProjectPerformed(av);
        } else if (isMayCreateProject()) {
            app.createProject().thenAccept(v -> {
                app.add(v);
                handleActionOnProjectPerformed((DocumentProject)v);//FIXME class cast exception
            });
        }
    }

    public void handleActionOnProjectPerformed(DocumentProject v) {
        if (!v.isDisabled()) {
            final Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
            /* Window wAncestor = v.getNode().getScene().getWindow(); */
            oldFocusOwner = getFocusOwner(v.getNode());
            v.addDisabler(this);
            if (v.isModified()) {
                URI unsavedURI = v.getURI();
                ButtonType[] options = { //
                    new ButtonType(labels.getString("file.saveBefore.saveOption.text"), ButtonBar.ButtonData.YES),//
                    new ButtonType(labels.getString("file.saveBefore.cancelOption.text"), ButtonBar.ButtonData.CANCEL_CLOSE), //
                    new ButtonType(labels.getString("file.saveBefore.dontSaveOption.text"), ButtonBar.ButtonData.NO)//
                };

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        labels.getString("file.saveBefore.doYouWantToSave.details"),
                        options);
                alert.setHeaderText(labels.getFormatted("file.saveBefore.doYouWantToSave.message",//
                        v.getTitle(), v.getDisambiguation()));

                alert.setOnHidden(dialogEvent -> {
                    ButtonType result = alert.getResult();
                    if (result != null) {
                        switch (result.getButtonData()) {
                            default:
                            case CANCEL_CLOSE:
                                v.removeDisabler(this);
                                if (oldFocusOwner != null) {
                                    oldFocusOwner.requestFocus();
                                }
                                break;
                            case NO:
                                doIt(v).whenComplete((r, e) -> {
                                    // FIXME check success
                                    v.removeDisabler(this);
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
                                saveView(v);
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
                    v.removeDisabler(this);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
                });
            }
        }
    }

    protected Node getFocusOwner(Node node) {

        Scene scene = node.getScene();
        return scene == null ? null : scene.getFocusOwner();
    }

    protected URIChooser getChooser(DocumentProject view) {
        URIChooser chsr = view.get(SAVE_CHOOSER_KEY);
        if (chsr == null) {
            chsr = getApplication().getModel().createSaveChooser();
            view.set(SAVE_CHOOSER_KEY, chsr);
        }
        return chsr;
    }

    protected void saveView(final DocumentProject v) {
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            //int option = fileChooser.showSaveDialog(this);

            URI uri = null;
            Outer:
            while (true) {
                uri = chooser.showDialog(v.getNode());

                // Prevent save to URI that is open in another view!
                // unless  multipe projects to same URI are supported
                if (uri != null
                        && !app.getModel().isAllowMultipleViewsPerURI()) {
                    for (Project vi : app.projects()) {
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
            if (uri != null) {
                saveViewToURI(v, uri, chooser);
            }
            v.removeDisabler(this);
            if (oldFocusOwner != null) {
                oldFocusOwner.requestFocus();
            }
        } else {
            saveViewToURI(v, v.getURI(), null);
        }
    }

    protected void saveViewToURI(final DocumentProject v, final URI uri, final URIChooser chooser) {
        v.write(uri, chooser == null ? null : chooser.getDataFormat()).handle((result, exception) -> {
            if (exception instanceof CancellationException) {
                v.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else if (exception != null) {
                Throwable value = exception;
                String message = (value.getMessage() != null) ? value.getMessage() : value.toString();
                Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        ((message == null) ? "" : message));
                alert.setHeaderText(labels.getFormatted("file.save.couldntSave.message", URIUtil.getName(uri)));
                alert.showAndWait();
                v.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else {
                v.setURI(uri);
                v.clearModified();
                v.setTitle(URIUtil.getName(uri));
                app.addRecentURI(uri);
                doIt(v);
            }
            return null;
        });
    }

    protected abstract CompletionStage<Void> doIt(DocumentProject p);
}
