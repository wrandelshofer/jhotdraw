/* @(#)ExportFileAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

import javafx.scene.control.Dialog;
import javafx.scene.input.DataFormat;
import javax.annotation.Nullable;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.DocumentOrientedActivity;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.collection.Key;

/**
 * Presents a file chooser to the user and then exports the contents of the
 * active view to the chosen file.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ExportFileAction extends AbstractSaveFileAction {

    public static final String ID = "file.export";
    private static final long serialVersionUID = 1L;

    private final Function<DataFormat,Dialog<Map<? super Key<?>, Object>>> optionsDialogFactory;

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public ExportFileAction(Application app) {
        this(app, null, ID, null);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public ExportFileAction(Application app, DocumentOrientedActivity view) {
        this(app, view, ID, null);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application, nonnull
     * @param optionsDialog the dialog for specifying export options
     */
    public ExportFileAction(Application app,  Function<DataFormat,Dialog<Map<? super Key<?>, Object>>> optionsDialog) {
        this(app, null, ID, optionsDialog);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application, nonnull
     * @param view the view, nullable
     * @param id the id, nonnull
     * @param optionsDialog the dialog for specifying export options
     */
    public ExportFileAction(Application app, DocumentOrientedActivity view, String id, Function<DataFormat, Dialog<Map<? super Key<?>, Object>>> optionsDialog) {
        super(app, view, id, true);
        this.optionsDialogFactory = optionsDialog;
    }

    @Override
    protected URIChooser createChooser(DocumentOrientedActivity view) {
        // XXX should be supplied to the action?
        return app.getModel().createExportChooser();
    }
@Nullable
@Override
    protected Dialog<Map<? super Key<?>, Object>> createOptionsDialog(DataFormat format) {
        return optionsDialogFactory==null?null:optionsDialogFactory.apply(format);
    }

    @Override
    protected void handleSucceded(DocumentOrientedActivity v, URI uri, DataFormat format) {
        // empty
    }
}
