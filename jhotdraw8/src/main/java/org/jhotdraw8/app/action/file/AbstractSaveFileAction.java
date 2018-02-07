/* @(#)AbstractSaveFileAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.input.DataFormat;
import javafx.stage.Modality;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractProjectAction;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.DocumentOrientedActivity;

/**
 * Saves the changes in the active view. If the active view has not an URI, an
 * {@code URIChooser} is presented.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractSaveFileAction extends AbstractProjectAction<DocumentOrientedActivity> {

    private static final long serialVersionUID = 1L;
    private boolean saveAs;
    private Node oldFocusOwner;
    private final Key<URIChooser> saveChooserKey = new ObjectKey<>("saveChooser", URIChooser.class);

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     * @param id the id
     * @param saveAs whether to force a file dialog
     */
    public AbstractSaveFileAction(Application app, DocumentOrientedActivity view, String id, boolean saveAs) {
        super(app, view, DocumentOrientedActivity.class);
        this.saveAs = saveAs;
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, id);
    }

    protected URIChooser getChooser(DocumentOrientedActivity view) {
        URIChooser c = view.get(saveChooserKey);
        if (c == null) {
            c = createChooser(view);
            view.set(saveChooserKey, c);
        }
        return c;
    }

    protected abstract URIChooser createChooser(DocumentOrientedActivity view);

    @Override
    protected void handleActionPerformed(ActionEvent evt, DocumentOrientedActivity v) {
        if (v == null) {
            return;
        }
        oldFocusOwner = v.getNode().getScene().getFocusOwner();
        v.addDisabler(this);
        saveProjectChooseUri(v);
    }

    protected void saveProjectChooseUri(final DocumentOrientedActivity v) {
        if (v.getURI() == null || saveAs) {
            URIChooser chsr = getChooser(v);
            //int option = fileChooser.showSaveDialog(this);

            URI uri = null;
            Outer:
            while (true) {
                uri = chsr.showDialog(v.getNode());

                // Prevent save to URI that is open in another view!
                // unless  multipe projects to same URI are supported
                if (uri != null && !app.getModel().isAllowMultipleViewsPerURI()) {
                    for (Activity pi : app.projects()) {
                        DocumentOrientedActivity vi = (DocumentOrientedActivity) pi;
                        if (vi != v && uri.equals(v.getURI())) {
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
                saveProjectChooseOptions(v, uri, chsr.getDataFormat());
            }else{
                v.removeDisabler(this);
            }
            if (oldFocusOwner != null) {
                oldFocusOwner.requestFocus();
            }
        } else {
            saveProjectChooseOptions(v, v.getURI(), v.getDataFormat());
        }
    }

    protected void saveProjectChooseOptions(final DocumentOrientedActivity v, URI uri, DataFormat format) {
        Map<? super Key<?>, Object> options = null;
        Dialog<Map<? super Key<?>, Object>> dialog = createOptionsDialog(format);
        if (dialog != null) {
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(v.getNode().getScene().getWindow());
            Optional< Map<? super Key<?>, Object>> result = dialog.showAndWait();

            if (result.isPresent()) {
                options = result.get();
            } else {
                v.removeDisabler(this);
                return;
            }
        }
        saveProjectToUri(v, uri, format, options);
    }

    protected void saveProjectToUri(final DocumentOrientedActivity project, final URI uri, final DataFormat format, Map<? super Key<?>, Object> options) {
        project.write(uri, format, options).handle((result, exception) -> {
            if (exception instanceof CancellationException) {
                project.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else if (exception != null) {
                Throwable value = exception;
                value.printStackTrace();
                Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
                Alert alert = new Alert(Alert.AlertType.ERROR, createErrorMessage(exception));
                alert.getDialogPane().setMaxWidth(640.0);
                alert.setHeaderText(labels.getFormatted("file.save.couldntSave.message", UriUtil.getName(uri)));
                alert.showAndWait();
                project.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else {
                handleSucceded(project, uri, format);
                project.removeDisabler(this);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            }
            return null;
        });
    }

    protected Dialog<Map<? super Key<?>, Object>> createOptionsDialog(DataFormat format) {
        return null;
    }

    protected abstract void handleSucceded(DocumentOrientedActivity v, URI uri, DataFormat format);
}
