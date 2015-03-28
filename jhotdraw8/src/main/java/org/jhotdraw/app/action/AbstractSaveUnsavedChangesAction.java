/*
 * @(#)AbstractSaveUnsavedChangesAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action;

import java.net.URI;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.collection.Key;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * This abstract class can be extended to implement an {@code Action} that asks
 to write unsaved changes of a {@link org.jhotdraw.app.View} before a destructive
 * action is performed.
 * <p>
 * If the view has no unsaved changes, method {@code doIt} is invoked immediately.
 If unsaved changes are present, a dialog is shown asking whether the user
 wants to discard the changes, cancel or write the changes before doing it.
 If the user chooses to discard the changes, {@code doIt} is invoked immediately.
 If the user chooses to cancel, the action is aborted.
 If the user chooses to write the changes, the view is saved, and {@code doIt}
 * is only invoked after the view was successfully saved.
 *
 * @author  Werner Randelshofer
 * @version $Id: AbstractSaveUnsavedChangesAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public abstract class AbstractSaveUnsavedChangesAction extends AbstractViewAction {

    /**
     *
     */
    public final static Key<Optional<URIChooser>> SAVE_CHOOSER_KEY = new Key<Optional<URIChooser>>(
            "saveChooser", Optional.class);

    private static final long serialVersionUID = 1L;

    private Optional<Node> oldFocusOwner = Optional.empty();

    /** Creates a new instance.
     * @param app the application 
     * @param view the view */
    public AbstractSaveUnsavedChangesAction(Application app, Optional<View> view) {
        super(app, view);
    }

    @Override
    public void handle(ActionEvent evt) {
        Application app = getApplication();
        Optional<View> av = getActiveView();
        if (!av.isPresent()) {
            if (isMayCreateView()) {
                av = Optional.of(app.getModel().createView());
                app.add(av.get());
            } else {
                return;
            }
        }
        final View v = av.get();
        if (!v.isDisabled()) {
            final ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
            /*Window wAncestor = v.getNode().getScene().getWindow();*/
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
                    boolean mustEnable = true;
                    ButtonType result = alert.getResult();
                    if (result != null) {
                        switch (result.getButtonData()) {
                            default:
                            case CANCEL_CLOSE:
                                v.removeDisabler(this);
                                break;
                            case NO:
                                doIt(v);
                                break;
                            case YES:
                                // this is a little bit quirky.
                                // saveView may start a worker thread
                                // and thus will enable the view at
                                // a later point in time.
                                mustEnable = false;
                                saveView(v);
                                break;
                        }
                    }
                    if (mustEnable) {
                        v.removeDisabler(this);
                        if (oldFocusOwner.isPresent()) {
                            oldFocusOwner.get().requestFocus();
                        }
                    }
                });
                alert.initOwner(v.getNode().getScene().getWindow());
                alert.initModality(Modality.WINDOW_MODAL);
                alert.show();
            } else {

                doIt(v);
                v.removeDisabler(this);
                if (oldFocusOwner.isPresent()) {
                    oldFocusOwner.get().requestFocus();
                }
            }
        }
    }

    protected Optional<
    Node> getFocusOwner(Node node) {

        Optional<Scene> scene = Optional.ofNullable(node.getScene());
        return Optional.ofNullable(scene.isPresent() ? scene.get().getFocusOwner() : null);
    }

    protected URIChooser getChooser(View view) {
        Optional<URIChooser> chsr = view.getValue(SAVE_CHOOSER_KEY);
        if (!chsr.isPresent()) {
            chsr = Optional.of(getApplication().getModel().createSaveChooser());
            view.putValue(SAVE_CHOOSER_KEY, chsr);
        }
        return chsr.get();
    }

    protected void saveView(final View v) {
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            //int option = fileChooser.showSaveDialog(this);

            Optional<URI> uri = Optional.empty();
            Outer:
            while (true) {
                uri = chooser.showDialog(v.getNode());

                // Prevent save to URI that is open in another view!
                // unless  multipe views to same URI are supported
                if (uri.isPresent() && !app.getModel().isAllowMultipleViewsPerURI()) {
                    for (View vi : app.views()) {
                        if (vi != v && v.getURI().equals(uri.get())) {
                            // FIXME Localize message
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You can not save to a file which is already open.");
                            alert.showAndWait();
                            continue Outer;
                        }
                    }
                }
                break;
            }
            if (uri.isPresent()) {
                saveViewToURI(v, uri.get(), chooser);
            }
            v.removeDisabler(this);
            if (oldFocusOwner.isPresent()) {
                oldFocusOwner.get().requestFocus();
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
                    if (oldFocusOwner.isPresent()) {
                        oldFocusOwner.get().requestFocus();
                    }
                    break;
                case FAILED:
                    Throwable value = event.getException();
                    String message = (value.getMessage() != null) ? value.getMessage() : value.toString();
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            ((message == null) ? "" : message));
                    alert.setHeaderText(labels.getFormatted("file.save.couldntSave.message", URIUtil.getName(uri)));
                    alert.showAndWait();
                    v.removeDisabler(this);
                    if (oldFocusOwner.isPresent()) {
                        oldFocusOwner.get().requestFocus();
                    }
                    break;
                case SUCCEEDED:
                    v.setURI(uri);
                    v.clearModified();
                    v.setTitle(URIUtil.getName(uri));
                    app.addRecentURI(uri);
                    doIt(v);
                    break;
                default:
                    break;
            }
        });
    }

    protected abstract void doIt(View p);
}
