/*
 * @(#)ExportFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractViewAction;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.URIUtil;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.ProjectView;

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
    public ExportFileAction(Application app) {
        this(app, null);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public ExportFileAction(Application app, ProjectView view) {
        this(app, view, ID);
    }
    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     * @param id the id
     */
    public ExportFileAction(Application app, ProjectView view, String id) {
        super(app, view, id, true);
    }
    @Override
    protected URIChooser createChooser(ProjectView view) {
        return app.getModel().createExportChooser();
    }
    @Override
    protected void handleSucceded(ProjectView v, URI uri) {
        // empty
    }
}
