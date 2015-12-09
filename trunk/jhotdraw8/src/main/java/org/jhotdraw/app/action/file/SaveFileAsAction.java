/*
 * @(#)SaveFileAsAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import org.jhotdraw.util.Resources;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;

/**
 * Presents an {@code URIChooser} and then saves the active view to the
 * specified location.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id: SaveFileAsAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class SaveFileAsAction extends SaveFileAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "file.saveAs";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public SaveFileAsAction(Application app, View view) {
        super(app, view, ID, true);
    }

    public SaveFileAsAction(Application app) {
        this(app, null);
    }
}
