/*
 * @(#)NewFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import javafx.event.ActionEvent;
import org.jhotdraw.util.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.action.AbstractApplicationAction;

/**
 * Creates a new view.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NewFileAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "file.new";

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public NewFileAction(Application app) {
        this(app, ID);
    }

    public NewFileAction(Application app, String id) {
        super(app);
        Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
        labels.configureAction(this, id);
    }

    @Override
    protected void onActionPerformed(ActionEvent evt) {
        Application app = getApplication();
        app.createView().thenAccept(newView -> {
            app.add(newView);
            newView.clear().thenRun(() -> {
                newView.clearModified();
            });
        });
    }
}
