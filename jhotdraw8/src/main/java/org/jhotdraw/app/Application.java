/* @(#)Application.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app;

import java.net.URI;
import java.util.function.Consumer;
import javafx.beans.property.IntegerProperty;
import org.jhotdraw.collection.HierarchicalMap;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SetProperty;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import org.jhotdraw.app.action.Action;
import org.jhotdraw.beans.PropertyBean;

/**
 * Application.
 *
 * @author Werner Randelshofer
 */
public interface Application extends Disableable, PropertyBean {

    public static final String RECENT_URIS_PROPERTY = "recentUris";
    public static final String MAX_NUMBER_OF_RECENT_URIS_PROPERTY = "maxNumberOfRecentUris";
    public static final String DISABLERS_PROPERTY = "disablers";
    public static final String DISABLED_PROPERTY = "disabled";

    /**
     * The set of views contains all visible views.
     *
     * @return the views
     */
    public SetProperty<View> viewsProperty();

    /**
     * The set of recent URIs. The set must be ordered by most recently used
     * first. Only the first items as specified in
     * {@link #maxNumberOfRecentUrisProperty} of the set are used and persisted
     * in user preferences.
     *
     * @return the recent Uris
     */
    public ReadOnlySetProperty<URI> recentUrisProperty();

    /**
     * The maximal number of recent URIs. Specifies how many items of
     * {@link #recentUrisProperty} are used and persisted in user preferences. This
     * number is also persisted.
     *
     * @return the number of recent Uris
     */
    public IntegerProperty maxNumberOfRecentUrisProperty();

    // Convenience method
    default public ObservableSet<View> views() {
        return viewsProperty().get();
    }

    /**
     * Adds the view to the set of views and shows it.
     *
     * @param v the view
     */
    default public void add(View v) {
        viewsProperty().add(v);
    }

    /**
     * Removes the view from the set of views and hides it.
     *
     * @param v the view
     */
    default public void remove(View v) {
        viewsProperty().remove(v);
    }

    /**
     * Provides the currently active view. This is the last view which was focus
     * owner. Returns null, if the application has no views.
     *
     * @return The active view.
     */
    public ReadOnlyObjectProperty<View> activeViewProperty();

    // Convenience method
    default public View getActiveView() {
        return activeViewProperty().get();
    }

    /**
     * Returns the action map of the application.
     *
     * @return the action map
     */
    public HierarchicalMap<String, Action> getActionMap();

    /**
     * Executes a worker on the thread pool of the application.
     *
     * @param r the runnable
     */
    public void execute(Runnable r);

    /**
     * Returns the application model.
     *
     * @return the model
     */
    public ApplicationModel getModel();

    /**
     * Sets the application model.
     *
     * @param newValue the model
     */
    public void setModel(ApplicationModel newValue);

    /**
     * Exits the application.
     */
    public void exit();

    /**
     * Returns the application node.
     *
     * @return the node
     */
    default public Node getNode() {
        return null;
    }

    /**
     * Creates a new view, initializes it, then invokes the callback.
     *
     * @param callback A callback. Can be null.
     */
    void createView(Consumer<View> callback);

    /**
     * Adds a recent URI.
     *
     * @param uri a recent URI
     */
    default void addRecentURI(URI uri) {
        recentUrisProperty().add(uri);
    }
}
