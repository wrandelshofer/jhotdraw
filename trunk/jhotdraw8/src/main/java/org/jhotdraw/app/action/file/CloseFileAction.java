/*
 * @(#)CloseFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.util.Optional;
import javafx.event.EventHandler;
import org.jhotdraw.util.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.DocumentOrientedApplication;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw.concurrent.TaskCompletionEvent;

/**
 * Closes the active view after letting the user save unsaved changes.
 *
 * @author  Werner Randelshofer
 * @version $Id: CloseFileAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class CloseFileAction extends AbstractSaveUnsavedChangesAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.close";

    /** Creates a new instance.
     * @param app the application
     * @param view the view */
    public CloseFileAction(Application app, Optional<View> view) {
        super(app, view);
        Resources labels = Resources.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    public CloseFileAction(DocumentOrientedApplication app) {
        this(app, Optional.empty());
    }

    @Override
    protected void doIt(View view, EventHandler<TaskCompletionEvent> callback) {
        if (view != null) {
            app.remove(view);
        }
        callback.handle(new TaskCompletionEvent());
    }
}
