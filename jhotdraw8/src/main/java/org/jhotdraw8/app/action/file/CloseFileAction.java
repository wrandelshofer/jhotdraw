/*
 * @(#)CloseFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.file;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.ProjectView;

/**
 * Closes the active view after letting the user save unsaved changes.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class CloseFileAction extends AbstractSaveUnsavedChangesAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.close";

    /** Creates a new instance.
     * @param app the application
     * @param view the view */
    public CloseFileAction(Application app, ProjectView view) {
        super(app, view);
        Resources.getResources("org.jhotdraw.app.Labels").configureAction(this, ID);
    }

    public CloseFileAction(Application app) {
        this(app, null);
    }

    @Override
    protected CompletionStage<Void> doIt(ProjectView view) {
        if (view != null) {
            app.remove(view);
        }
        return CompletableFuture.completedFuture(null);
    }
}
