/*
 * @(#)SaveFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.net.URI;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
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
public class SaveFileAction extends AbstractSaveFileAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.save";

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public SaveFileAction(Application app) {
        this(app, null, false);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public SaveFileAction(Application app, ProjectView view) {
        this(app, view, false);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     * @param saveAs whether to force a file dialog
     */
    public SaveFileAction(Application app, ProjectView view, boolean saveAs) {
        this(app, view, ID, saveAs);
    }
    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     * @param id the id
     * @param saveAs whether to force a file dialog
     */
    public SaveFileAction(Application app, ProjectView view, String id, boolean saveAs) {
        super(app, view, id, saveAs);
    }

    @Override
    protected URIChooser createChooser(ProjectView view) {
        return app.getModel().createSaveChooser();
    }

    @Override
    protected void handleSucceded(ProjectView v, URI uri) {
        v.setURI(uri);
        v.clearModified();
        v.setTitle(URIUtil.getName(uri));
        app.addRecentURI(uri);
    }

}
