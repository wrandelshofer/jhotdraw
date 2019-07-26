/*
 * @(#)AbstractSaveFileAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.input.DataFormat;
import javafx.stage.Modality;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.DocumentBasedActivity;
import org.jhotdraw8.app.action.AbstractViewControllerAction;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.concurrent.SimpleWorkState;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.UriUtil;
import org.jhotdraw8.util.Resources;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;

/**
 * Saves the changes in the active view. If the active view has not an URI, an
 * {@code URIChooser} is presented.
 * <p>
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractSaveFileAction extends AbstractViewControllerAction<DocumentBasedActivity> {

    private static final long serialVersionUID = 1L;
    private boolean saveAs;
    private Node oldFocusOwner;
    private final Key<URIChooser> saveChooserKey = new ObjectKey<>("saveChooser", URIChooser.class);

    /**
     * Creates a new instance.
     *
     * @param app    the application
     * @param view   the view
     * @param id     the id
     * @param saveAs whether to force a file dialog
     */
    public AbstractSaveFileAction(Application app, DocumentBasedActivity view, String id, boolean saveAs) {
        this(app, view, id, saveAs, ApplicationLabels.getResources());
    }

    /**
     * Creates a new instance.
     *
     * @param app       the application
     * @param view      the view
     * @param id        the id
     * @param saveAs    whether to force a file dialog
     * @param resources the resources are used for setting labels and icons for the action
     */
    public AbstractSaveFileAction(Application app, DocumentBasedActivity view, String id, boolean saveAs, Resources resources) {
        super(app, view, DocumentBasedActivity.class);
        this.saveAs = saveAs;
        resources.configureAction(this, id);
    }

    protected URIChooser getChooser(@Nonnull DocumentBasedActivity view) {
        URIChooser c = view.get(saveChooserKey);
        if (c == null) {
            c = createChooser(view);
            view.set(saveChooserKey, c);
        }
        return c;
    }

    protected abstract URIChooser createChooser(DocumentBasedActivity view);

    @Override
    protected void handleActionPerformed(ActionEvent evt, @Nullable DocumentBasedActivity v) {
        if (v == null) {
            return;
        }
        oldFocusOwner = v.getNode().getScene().getFocusOwner();
        WorkState workState = new SimpleWorkState(getLabel());
        v.addDisabler(workState);
        saveFileChooseUri(v, workState);
    }

    protected void saveFileChooseUri(@Nonnull final DocumentBasedActivity v, WorkState workState) {
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
                    for (Activity pi : app.views()) {
                        DocumentBasedActivity vi = (DocumentBasedActivity) pi;
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
                saveFileChooseOptions(v, uri, chsr.getDataFormat(), workState);
            } else {
                v.removeDisabler(workState);
            }
            if (oldFocusOwner != null) {
                oldFocusOwner.requestFocus();
            }
        } else {
            saveFileChooseOptions(v, v.getURI(), v.getDataFormat(), workState);
        }
    }

    protected void saveFileChooseOptions(@Nonnull final DocumentBasedActivity v, @Nonnull URI uri, DataFormat format, WorkState workState) {
        Map<? super Key<?>, Object> options = null;
        Dialog<Map<? super Key<?>, Object>> dialog = null;
        try {
            dialog = createOptionsDialog(format);
        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, createErrorMessage(e));
            alert.getDialogPane().setMaxWidth(640.0);
            Resources labels = ApplicationLabels.getResources();
            alert.setHeaderText(labels.getFormatted("file.save.couldntSave.message", UriUtil.getName(uri)));
            alert.showAndWait();
            v.removeDisabler(this);
            return;
        }
        if (dialog != null) {
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(v.getNode().getScene().getWindow());
            Optional<Map<? super Key<?>, Object>> result = dialog.showAndWait();

            if (result.isPresent()) {
                options = result.get();
            } else {
                v.removeDisabler(workState);
                return;
            }
        }
        saveFileToUri(v, uri, format, options, workState);
    }

    protected void saveFileToUri(@Nonnull final DocumentBasedActivity view, @Nonnull final URI uri, final DataFormat format, Map<? super Key<?>, Object> options, WorkState workState) {
        view.write(uri, format, options, workState).handle((result, exception) -> {
            if (exception instanceof CancellationException) {
                view.removeDisabler(workState);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else if (exception != null) {
                Throwable value = exception;
                value.printStackTrace();
                Resources labels = ApplicationLabels.getResources();
                Alert alert = new Alert(Alert.AlertType.ERROR, createErrorMessage(exception));
                alert.getDialogPane().setMaxWidth(640.0);
                alert.setHeaderText(labels.getFormatted("file.save.couldntSave.message", UriUtil.getName(uri)));
                alert.showAndWait();
                view.removeDisabler(workState);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            } else {
                handleSucceeded(view, uri, format);
                view.removeDisabler(workState);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            }
            return null;
        });
    }

    @Nullable
    protected Dialog<Map<? super Key<?>, Object>> createOptionsDialog(DataFormat format) {
        return null;
    }

    protected abstract void handleSucceeded(DocumentBasedActivity v, URI uri, DataFormat format);
}
