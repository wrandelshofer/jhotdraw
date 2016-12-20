/*
 * @(#)AbstractFindAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.edit;

import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ProjectView;
import org.jhotdraw8.app.action.AbstractViewAction;
import org.jhotdraw8.util.Resources;

/**
 * Presents a find dialog to the user and then highlights the found items in the
 * active view.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFindAction<V extends ProjectView<V>> extends AbstractViewAction<V> {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.find";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public AbstractFindAction(Application<V> app, V view) {
        super(app, view);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }
}
