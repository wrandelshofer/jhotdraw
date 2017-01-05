/*
 * @(#)ExportFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.app.DocumentProject;

/**
 * Presents a file chooser to the user and then exports the contents of the
 * active view to the chosen file.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ExportFileAction extends AbstractSaveFileAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.export";

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public ExportFileAction(Application<DocumentProject> app) {
        this(app, null);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public ExportFileAction(Application<DocumentProject> app, DocumentProject view) {
        this(app, view, ID);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     * @param id the id
     */
    public ExportFileAction(Application<DocumentProject> app, DocumentProject view, String id) {
        super(app, view, id, true);
    }

    @Override
    protected URIChooser createChooser(DocumentProject view) {
        return app.getModel().createExportChooser();
    }

    @Override
    protected void handleSucceded(DocumentProject v, URI uri) {
        // empty
    }
}
