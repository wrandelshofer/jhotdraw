/*
 * @(#)ClearFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.app.action.file;

import javafx.event.EventHandler;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw.concurrent.TaskCompletionEvent;
import org.jhotdraw.util.Resources;

/**
 * Clears (empties) the contents of the active view.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id: ClearFileAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class ClearFileAction extends AbstractSaveUnsavedChangesAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "file.clear";
    
    /** Creates a new instance.
     * @param app the application
     * @param view the view */
    public ClearFileAction(Application app, View view) {
        super(app, view);
        Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
        labels.configureAction(this, "file.clear");
    }
    
    @Override public void doIt(final View view, EventHandler<TaskCompletionEvent<?>> callback) {
        view.clear(callback);
    }
}
