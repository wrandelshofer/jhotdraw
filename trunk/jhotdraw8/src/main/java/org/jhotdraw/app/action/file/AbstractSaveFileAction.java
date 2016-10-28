/*
 * @(#)AbstractSaveFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.net.URI;
import java.util.concurrent.CancellationException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.DataFormat;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.SimpleKey;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.Resources;
import org.jhotdraw.app.ProjectView;

/**
 * Saves the changes in the active view. If the active view has not an URI, an
 * {@code URIChooser} is presented.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractSaveFileAction extends AbstractViewAction {

    private static final long serialVersionUID = 1L;
    private boolean saveAs;
    private Node oldFocusOwner;
    private final Key<URIChooser> saveChooserKey = new SimpleKey<>("saveChooser", URIChooser.class);

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     * @param id the id
     * @param saveAs whether to force a file dialog
     */
    public AbstractSaveFileAction(Application app, ProjectView view, String id, boolean saveAs) {
        super(app, view);
        this.saveAs = saveAs;
        Resources.getResources("org.jhotdraw.app.Labels").configureAction(this, id);
    }

    protected URIChooser getChooser(ProjectView view) {
        URIChooser c = view.get(saveChooserKey);
        if (c == null) {
            c = createChooser(view);
            view.set(saveChooserKey, c);
        }
        return c;
    }

    protected abstract URIChooser createChooser(ProjectView view);

    @Override
    protected void onActionPerformed(ActionEvent evt) {
        if (isDisabled()) {
            return;
        }
        final ProjectView v = getActiveView();
        if (v == null || v.isDisabled()) {
            return;
        }
        oldFocusOwner = v.getNode().getScene().getFocusOwner();
        v.addDisabler(this);
        saveView(v);
    }

    protected void saveView(final ProjectView v) {
        if (v.getURI() == null || saveAs) {
            URIChooser chsr = getChooser(v);
            //int option = fileChooser.showSaveDialog(this);

            URI uri = null;
            Outer:
            while (true) {
                uri = chsr.showDialog(v.getNode());

                // Prevent save to URI that is open in another view!
                // unless  multipe views to same URI are supported
                if (uri != null && !app.getModel().isAllowMultipleViewsPerURI()) {
                    for (ProjectView vi : app.views()) {
                        if (vi != v && uri.equals(v.getURI())) {
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
                saveViewToURI(v, uri, chsr.getDataFormat());
            }
            v.removeDisabler(this);
            if (oldFocusOwner != null) {
                oldFocusOwner.requestFocus();
            }
        } else {
            saveViewToURI(v, v.getURI(), null);
        }
    }

    protected void saveViewToURI(final ProjectView v, final URI uri, final DataFormat format) {
        v.write(uri,format).handle((result, exception) -> {
            if (exception instanceof CancellationException) {
                    v.removeDisabler(this);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
            } else if (exception != null) {
                    Throwable value = exception;
                    value.printStackTrace();
                    String message = (value != null && value.getMessage() != null) ? value.getMessage() : value.toString();
                    Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            ((message == null) ? "" : message));
                    alert.setHeaderText(labels.getFormatted("file.save.couldntSave.message", URIUtil.getName(uri)));
                    alert.showAndWait();
                    v.removeDisabler(this);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
            } else {
                    handleSucceded(v, uri);
                    v.removeDisabler(this);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
            }
            return null;
        });
    }

    protected abstract void handleSucceded(ProjectView v, URI uri);
}
