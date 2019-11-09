/*
 * @(#)ApplicationModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.scene.control.MenuBar;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.concurrent.FXWorker;
import org.jhotdraw8.gui.URIChooser;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionStage;
import java.util.prefs.Preferences;

/**
 * {@code ApplicationModel} provides meta-data, actions and factory methods for
 * an {@link Application}.
 *
 * @author Werner Randelshofer.
 * @design.pattern Application Framework, KeyAbstraction.
 */
public interface ApplicationModel {

    default CompletionStage<Activity> createActivityAsync() {
        return FXWorker.supply(this::createActivity);
    }

    /**
     * Instantiates a view. But does not initialize it.
     * <p>
     * Since this operation may involve class loading, it should be performed in
     * the background.
     *
     * @return a new instance
     */
    Activity createActivity();

    /**
     * Creates an open chooser.
     *
     * @return chooser
     */
    URIChooser createOpenChooser();

    /**
     * Creates a save chooser.
     *
     * @return chooser
     */
    URIChooser createSaveChooser();

    /**
     * Creates an export chooser.
     *
     * @return chooser
     */
    URIChooser createExportChooser();

    /**
     * Creates an import chooser.
     *
     * @return chooser
     */
    URIChooser createImportChooser();

    /**
     * Returns the name of the application.
     *
     * @return name
     */
    String getName();

    /**
     * Returns the version of the application.
     *
     * @return version or null if unknown
     */
    @Nullable
    String getVersion();

    /**
     * Returns the vendor of the application.
     * (this is typically the vendor or author of the application).
     *
     * @return vendor or null if unknown
     */
    @Nullable
    String getVendor();

    /**
     * Returns the license of the application
     *
     * @return the license or null if unknown
     */
    @Nullable
    String getLicense();

    /**
     * Returns true if the same URI can be opened more than once.
     *
     * @return whether multiple views per URI are allowed
     */
    boolean isAllowMultipleViewsPerURI();

    /**
     * Creates a menu bar. This method is invoked by {@code Application} when it
     * needs to create a menu bar. {@code Application} uses the {@code id} of
     * the menu items in the menu bar to link the menu item with {@code Action}
     * objects.
     *
     * @return a menu bar
     */
    default CompletionStage<MenuBar> createMenuBarAsync() {
        return FXWorker.supply(this::createMenuBar);
    }

    MenuBar createMenuBar();

    /**
     * Gets the resource bundle of the application.
     *
     * @return the resource bundle
     */
    ResourceBundle getResources();

    /**
     * Creates the application map which is used to populate menu bars.
     *
     * @param app The application
     * @return the application map
     */
    HierarchicalMap<String, Action> createApplicationActionMap(Application app);

    /**
     * Gets the preferences of the application.
     *
     * @return the preferences
     */
    Preferences getPreferences();

    /**
     * Returns a list of stylesheets that are added to all scenes created
     * by the application.
     *
     * @return list of scene stylesheets
     */
    default List<String> getSceneStylesheets() {
        return Collections.emptyList();
    }


}
