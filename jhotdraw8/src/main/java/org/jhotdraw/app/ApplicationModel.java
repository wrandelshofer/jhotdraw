/* @(#)ApplicationModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app;

import java.util.ResourceBundle;
import javafx.scene.control.MenuBar;
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
    /** Instantiates a view. But does not initialize it.
     *
     * Since this operation may involve class loading, it should be performed
     * in the background.
     * 
     * @return a new instance
     */
    public View instantiateView();
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

    /** Creates a menu bar.
     * This method is invoked by {@code Application} when it needs to create
     * a menu bar. {@code Application} uses the {@code id} of the menu items
     * in the menu bar to link the menu item with {@code Action} objects.
     * @return a menu bar
     */
    MenuBar createMenuBar();
    
    /** Gets the resource bundle for use by the application.
     * @return the resource bundle */
    ResourceBundle getResources();
}
