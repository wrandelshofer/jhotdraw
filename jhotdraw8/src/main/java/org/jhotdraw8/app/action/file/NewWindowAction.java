/*
 * @(#)NewFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw8.app.action.file;

import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.DocumentView;

/**
 * Creates a new view.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NewWindowAction extends NewFileAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "file.newWindow";
    
    /** Creates a new instance.
     * @param app the application */
    public NewWindowAction(Application<DocumentView> app) {
        super(app, ID);
    }
}
