/*
 * @(#)AbstractPreferencesAction.java
 * 
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.app;

import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.util.Resources;

/**
 * Displays a preferences dialog for the application.
 * <p>

 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractPreferencesAction extends AbstractApplicationAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "application.preferences";

    /** Creates a new instance.
     * @param app the application */
    public AbstractPreferencesAction(Application app) {
        super(app);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }
}
