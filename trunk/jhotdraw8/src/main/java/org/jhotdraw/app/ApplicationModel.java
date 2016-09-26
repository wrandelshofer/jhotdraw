/* @(#)ApplicationModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app;

import java.util.ResourceBundle;
import javafx.scene.control.MenuBar;
import org.jhotdraw.app.action.Action;
import org.jhotdraw.collection.HierarchicalMap;
import org.jhotdraw.gui.URIChooser;

/**
 * {@code ApplicationModel} provides meta-data,
 * actions and factory methods for an {@link Application}.
 *
 * @design.pattern Application Framework, KeyAbstraction.
 * 
 * @author Werner Randelshofer.
 * @version $Id$
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
    public ProjectView instantiateView();
    // URI choosers

    /** Creates an open chooser.
     * @return chooser */
    public URIChooser createOpenChooser();

    /** Creates a save chooser. 
     * @return chooser */
    public URIChooser createSaveChooser();
    /** Creates an export chooser. 
     * @return chooser */
    public URIChooser createExportChooser();
    /** Creates an import chooser. 
     * @return chooser */
    public URIChooser createImportChooser();

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
    
    /**
     * Creates the application map which is used to populate menu bars.
     * 
     * @param app The application
     * @return the application map
     */
    HierarchicalMap<String, Action> createApplicationActionMap(Application app);
}
