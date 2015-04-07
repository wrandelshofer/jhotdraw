/*
 * @(#)SaveFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.net.URI;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.collection.Key;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.*;

/**
 * Saves the changes in the active view. If the active view has not an URI,
 * an {@code URIChooser} is presented.
 * <p>
 *
 * @author  Werner Randelshofer
 * @version $Id: SaveFileAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class SaveFileAction extends AbstractViewAction {

    private static final long serialVersionUID = 1L;
    public final static Key<URIChooser> SAVE_CHOOSER_KEY = new Key<>("saveChooser", URIChooser.class);

    public static final String ID = "file.save";
    private boolean saveAs;
    private Node oldFocusOwner;

    /** Creates a new instance.
     * @param app the application
     */
    public SaveFileAction(Application app) {
        this(app, Optional.empty(), false);
    }

    /** Creates a new instance.
     * @param app the application
     * @param view the view */
    public SaveFileAction(Application app, Optional<View> view) {
        this(app, view, false);
    }

    /** Creates a new instance. 
     * @param app the application 
     * @param view the view
     * @param saveAs whether to force a file dialog */
    public SaveFileAction(Application app, Optional<View> view, boolean saveAs) {
        super(app, view);
        this.saveAs = saveAs;
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    protected URIChooser getChooser(View view) {
        URIChooser c = app.get(SAVE_CHOOSER_KEY);
        if (c == null) {
            c = getApplication().getModel().createSaveChooser();
            app.set(SAVE_CHOOSER_KEY, c);
        }
        return c;
    }

    @Override
    public void handle(ActionEvent evt) {
        if (isDisabled()) {
            return;
        }
        final Optional<View> v = getActiveView();
        if (!v.isPresent() || v.get().isDisabled()) {
            return;
        }
        oldFocusOwner = v.get().getNode().getScene().getFocusOwner();
        v.get().addDisabler(this);
        saveView(v.get());
    }

    protected void saveView(final View v) {
        if (v.getURI() == null || saveAs) {
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
                        if (vi != v && uri.get().equals(v.getURI())) {
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
                saveViewToURI(v, uri.get(), Optional.of(chooser));
            }
            v.removeDisabler(this);
            if (oldFocusOwner != null) {
                oldFocusOwner.requestFocus();
            }
        } else {
            saveViewToURI(v, v.getURI(), null);
        }
    }

    protected void saveViewToURI(final View v, final URI uri, final Optional<URIChooser> chooser) {
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
                    String message = (value != null && value.getMessage() != null) ? value.getMessage() : value.toString();
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
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
                    v.removeDisabler(this);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
                    break;
                default:
                    break;
            }
        });
    }
}
