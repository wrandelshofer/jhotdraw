/*
 * @(#)ClearFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.app.action.file;

import java.util.concurrent.CompletionStage;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw.util.Resources;
import org.jhotdraw.app.ProjectView;

/**
 * Clears (empties) the contents of the active view.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ClearFileAction extends AbstractSaveUnsavedChangesAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "file.clear";
    
    /** Creates a new instance.
     * @param app the application
     * @param view the view */
    public ClearFileAction(Application app, ProjectView view) {
        super(app, view);
        Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
        labels.configureAction(this, "file.clear");
    }
    
    @Override public CompletionStage<Void> doIt(final ProjectView view) {
        return view.clear();
    }
}
