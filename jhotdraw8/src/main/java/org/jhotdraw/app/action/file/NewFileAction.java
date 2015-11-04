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
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractApplicationAction;

/**
 * Creates a new view.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id: NewFileAction.java 788 2014-03-22 07:56:28Z rawcoder $
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
        app.getModel().createView(newView -> {
            /*
             int multiOpenId = 1;
             for (View existingP : app.views()) {
             if (existingP.getURI() == null) {
             multiOpenId = Math.max(multiOpenId, existingP.getMultipleOpenId() + 1);
             }
             }
             newView.setMultipleOpenId(multiOpenId);
             */
            app.add(newView);
            newView.clear(e -> {
                newView.clearModified();
            });
        });
    }
}
