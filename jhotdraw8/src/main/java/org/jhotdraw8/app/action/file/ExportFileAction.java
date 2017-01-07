/*
 * @(#)ExportFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.scene.control.Dialog;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.app.DocumentProject;
import org.jhotdraw8.collection.Key;

/**
 * Presents a file chooser to the user and then exports the contents of the
 * active project to the chosen file.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ExportFileAction extends AbstractSaveFileAction {

    public static final String ID = "file.export";
    private static final long serialVersionUID = 1L;

    private final Function<DataFormat,Dialog<Map<? super Key<?>, Object>>> optionsDialog;

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
     * @param project the project
     */
    public ExportFileAction(Application app, DocumentProject project) {
        this(app, project, ID, null);
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
     * @param project the project, nullable
     * @param id the id, nonnull
     * @param optionsDialog the dialog for specifying export options
     */
    public ExportFileAction(Application app, DocumentProject project, String id,  Function<DataFormat, Dialog<Map<? super Key<?>, Object>>> optionsDialog) {
        super(app, project, id, true);
        this.optionsDialog = optionsDialog;
    }

    @Override
    protected URIChooser createChooser(DocumentProject project) {
        // XXX should be supplied to the action?
        return app.getModel().createExportChooser();
    }
@Override
    protected Dialog<Map<? super Key<?>, Object>> createOptionsDialog(DataFormat format) {
        return optionsDialog.apply(format);
    }

    @Override
    protected void handleSucceded(DocumentProject v, URI uri) {
        // empty
    }
}
