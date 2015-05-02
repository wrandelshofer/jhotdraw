/*
 * @(#)AbstractPreferencesAction.java
 * 
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.app;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.action.AbstractApplicationAction;
import org.jhotdraw.util.Resources;

/**
 * Displays a preferences dialog for the application.
 * <p>

 * @author Werner Randelshofer
 * @version $Id: AbstractPreferencesAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public abstract class AbstractPreferencesAction extends AbstractApplicationAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "application.preferences";

    /** Creates a new instance.
     * @param app the application */
    public AbstractPreferencesAction(Application app) {
        super(app);
        Resources labels = Resources.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
}
