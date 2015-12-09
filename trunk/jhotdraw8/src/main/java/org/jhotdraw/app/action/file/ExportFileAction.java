/*
 * @(#)ExportFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.net.URI;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.Resources;

/**
 * Presents a file chooser to the user and then exports the contents of the
 * active view to the chosen file.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id: ExportFileAction.java 788 2014-03-22 07:56:28Z rawcoder $
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
    public ExportFileAction(Application app, View view) {
        this(app, view, ID);
    }
    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     * @param saveAs whether to force a file dialog
     */
    public ExportFileAction(Application app, View view, String id) {
        super(app, view, id, true);
    }
    @Override
    protected URIChooser createChooser(View view) {
        return app.getModel().createExportChooser();
    }
    @Override
    protected void handleSucceded(View v, URI uri) {
        // empty
    }
}
