/*
 * @(#)AbstractSaveUnsavedChangesAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app.action;

import java.net.URI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.collection.Key;
import org.jhotdraw.concurrent.TaskCompletionEvent;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.Resources;

/**
 * This abstract class can be extended to implement an {@code Action} that asks
 * to write unsaved changes of a {@link org.jhotdraw.app.View} before a
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
public abstract class AbstractSaveUnsavedChangesAction extends AbstractViewAction {

    /**
     *
     */
    public final static Key<URIChooser> SAVE_CHOOSER_KEY = new Key<URIChooser>(
            "saveChooser", URIChooser.class, null);

    private static final long serialVersionUID = 1L;

    private Node oldFocusOwner = null;

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public AbstractSaveUnsavedChangesAction(Application app, View view) {
        super(app, view);
    }

    @Override
    public void handle(ActionEvent evt) {
        Application app = getApplication();
        View av = getActiveView();
        if (av != null) {
            handle(av);
        } else if (isMayCreateView()) {
            app.getModel().createView(v -> {
                app.add(v);
                handle(v);
            });
        }
    }

    public void handle(View v) {
        if (!v.isDisabled()) {
            final Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
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
                                doIt(v, e -> {
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
                alert.initOwner(v.getNode().getScene().getWindow());
                alert.initModality(Modality.WINDOW_MODAL);
                alert.show();
            } else {

                doIt(v, e -> {
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

    protected URIChooser getChooser(View view) {
        URIChooser chsr = view.get(SAVE_CHOOSER_KEY);
        if (chsr == null) {
            chsr = getApplication().getModel().createSaveChooser();
            view.set(SAVE_CHOOSER_KEY, chsr);
        }
        return chsr;
    }

    protected void saveView(final View v) {
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            //int option = fileChooser.showSaveDialog(this);

            URI uri = null;
            Outer:
            while (true) {
                uri = chooser.showDialog(v.getNode());

                // Prevent save to URI that is open in another view!
                // unless  multipe views to same URI are supported
                if (uri != null
                        && !app.getModel().isAllowMultipleViewsPerURI()) {
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

    protected void saveViewToURI(final View v, final URI uri, final URIChooser chooser) {
        v.write(uri, event -> {
            switch (event.getState()) {
                case CANCELLED:
                    v.removeDisabler(this);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
                    break;
                case FAILED:
                    Throwable value = event.getException();
                    String message = (value.getMessage() != null) ? value.getMessage() : value.toString();
                    Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            ((message == null) ? "" : message));
                    alert.setHeaderText(labels.getFormatted("file.save.couldntSave.message", URIUtil.getName(uri)));
                    alert.showAndWait();
                    v.removeDisabler(this);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
                    break;
                case SUCCEEDED:
                    v.setURI(uri);
                    v.clearModified();
                    v.setTitle(URIUtil.getName(uri));
                    app.addRecentURI(uri);
                    doIt(v, e -> {
                    });
                    break;
                default:
                    break;
            }
        });
    }

    protected abstract void doIt(View p, EventHandler<TaskCompletionEvent> callback);
}
