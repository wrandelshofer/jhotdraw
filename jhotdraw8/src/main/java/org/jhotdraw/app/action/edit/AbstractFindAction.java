/*
 * @(#)AbstractFindAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.app.action.edit;

import java.util.Optional;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.util.*;

/**
 * Presents a find dialog to the user and then highlights the found items
 * in the active view.
 *
 * @author Werner Randelshofer
 * @version $Id: AbstractFindAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public abstract class AbstractFindAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.find";
    
    /** Creates a new instance.
     * @param app the application
     * @param view the view */
    public AbstractFindAction(Application app, Optional<View> view) {
        super(app, view);
        Resources labels = Resources.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }    
}
