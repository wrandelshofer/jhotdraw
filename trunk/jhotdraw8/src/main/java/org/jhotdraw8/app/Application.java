/* @(#)Application.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app;

import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import javafx.beans.property.IntegerProperty;
import org.jhotdraw8.collection.HierarchicalMap;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SetProperty;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.beans.PropertyBean;

/**
 * An {@code Application} manages {@link ProjectView}s.
 *
 * @param <V> the type of project views that this application manages.
 * @design.pattern Application Framework, KeyAbstraction. The application
 * framework supports the creation of document oriented applications which can
 * support platform-specific guidelines. The application framework consists of
 * the following key abstractions:  {@link Application}, {@link ApplicationModel}, {@link ProjectView}, 
 * {@link Action}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Application<V extends ProjectView<V>> extends Disableable, PropertyBean {

    public static final String RECENT_URIS_PROPERTY = "recentUris";
    public static final String MAX_NUMBER_OF_RECENT_URIS_PROPERTY = "maxNumberOfRecentUris";

    /**
     * The set of views contains all visible views.
     *
     * @return the views
     */
    public SetProperty<V> viewsProperty();

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
     * {@link #recentUrisProperty} are used and persisted in user preferences.
     * This number is also persisted.
     *
     * @return the number of recent Uris
     */
    public IntegerProperty maxNumberOfRecentUrisProperty();

    // Convenience method
    default public ObservableSet<V> views() {
        return viewsProperty().get();
    }

    /**
     * Adds the view to the set of views and shows it.
     *
     * @param v the view
     */
    default public void add(V v) {
        viewsProperty().add(v);
    }

    /**
     * Removes the view from the set of views and hides it.
     *
     * @param v the view
     */
    default public void remove(V v) {
        viewsProperty().remove(v);
    }

    /**
     * Provides the currently active view. This is the last view which was focus
     * owner. Returns null, if the application has no views.
     *
     * @return The active view.
     */
    public ReadOnlyObjectProperty<V> activeViewProperty();

    // Convenience method
    default public V getActiveView() {
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
    public ApplicationModel<V> getModel();

    /**
     * Sets the application model.
     *
     * @param newValue the model
     */
    public void setModel(ApplicationModel<V> newValue);

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
     * @return A callback.
     */
    CompletionStage<V> createView();

    /**
     * Adds a recent URI.
     *
     * @param uri a recent URI
     */
    default void addRecentURI(URI uri) {
        // ensures that the last used uri lands at the end of the LinkedHashSet.
        Set<URI> recents = recentUrisProperty().get();
        recents.remove(uri);
        recents.add(uri);
        if (recents.size() > getMaxNumberOfRecentUris()) {
            Iterator<URI> i = recents.iterator();
            i.next();
            i.remove();
        }
    }

    default int getMaxNumberOfRecentUris() {
        return maxNumberOfRecentUrisProperty().get();
    }
}
