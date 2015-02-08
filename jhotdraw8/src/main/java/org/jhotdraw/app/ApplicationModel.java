/* @(#)ApplicationModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app;

import org.jhotdraw.gui.URIChooser;

/**
 * {@code ApplicationModel} provides meta-data for an {@link Application},
 * actions and factory methods for creating {@link View}s, toolbars and
 * {@link URIChooser}s.
 *
 * @author Werner Randelshofer.
 * @version $Id: ApplicationModel.java 789 2014-03-22 13:14:07Z rawcoder $
 */
public interface ApplicationModel {
    // Views
    /** Creates a view and initializes it.
     * @return the view */
    public View createView();
    // URI choosers
    
    /** Creates an open chooser.
     * @return chooser */
    public URIChooser createOpenChooser();

    /** Creates a save chooser. 
     * @return chooser */
    public URIChooser createSaveChooser();

    // Copyright information
    
    /** Returns the name of the application.
     * @return  name */
    public String getName();

    /** Returns the version of the application.
     * @return  version */
    public String getVersion();

    /** Returns the copyright of the application.
     * @return  copyright */
    public String getCopyright();

    /** Returns true if the same URI can be opened more than once.
     * @return whether multiple views per URI are allowed */
    public boolean isAllowMultipleViewsPerURI();

}
