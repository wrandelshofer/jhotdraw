/* @(#)Application.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import java.net.URI;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SetProperty;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import javafx.scene.input.DataFormat;
import javax.annotation.Nullable;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.HierarchicalMap;

/**
 * An {@code Application} handles the life-cycle of {@link ViewController} objects and
 * provides windows to present them on screen.
 *
 * @design.pattern Application Framework, KeyAbstraction. The application
 * framework supports the creation of document oriented applications which can
 * support platform-specific guidelines. The application framework consists of
 * the following key abstractions: null {@link Application}, {@link ApplicationModel}, {@link ViewController},
 * {@link Action}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Application extends Disableable, PropertyBean {

    public static final String RECENT_URIS_PROPERTY = "recentUris";
    public static final String MAX_NUMBER_OF_RECENT_URIS_PROPERTY = "maxNumberOfRecentUris";
    public static final String MODEL_PROPERTY = "model";

    /**
     * The application model.
     *
     * @return the model
     */
    public ObjectProperty<ApplicationModel> modelProperty();

    /**
     * The set of views contains all open views..
     *
     * @return the views
     */
    public SetProperty<ViewController> viewsProperty();

    /**
     * The set of recent URIs. The set must be ordered by most recently used
     * first. Only the first items as specified in
     * {@link #maxNumberOfRecentUrisProperty} of the set are used and persisted
     * in user preferences.
     *
     * @return the recent Uris
     */
    public ReadOnlySetProperty<Map.Entry<URI,DataFormat>> recentUrisProperty();

    /**
     * The maximal number of recent URIs. Specifies how many items of
     * {@link #recentUrisProperty} are used and persisted in user preferences.
     * This number is also persisted.
     *
     * @return the number of recent Uris
     */
    public IntegerProperty maxNumberOfRecentUrisProperty();

    // Convenience method
    default public ObservableSet<ViewController> views() {
        return viewsProperty().get();
    }

    /**
     * Adds the view to the set of views and shows it.
     *
     * @param v the view
     */
    default public void add(ViewController v) {
        viewsProperty().add(v);
    }

    /**
     * Removes the view from the set of visible views and hides it.
     *
     * @param v the view
     */
    default public void remove(ViewController v) {
        viewsProperty().remove(v);
    }

    /**
     * Provides the currently active view. This is the last view which was
    * focus owner. Returns null, if the application has no views.
     *
     * @return The active view.
     */
    public ReadOnlyObjectProperty<ViewController> activeViewProperty();

    // Convenience method
    @Nullable
    default public ViewController getActiveView() {
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
    default ApplicationModel getModel() {
        return modelProperty().get();
    }

    /**
     * Sets the application model.
     *
     * @param newValue the model
     */
    default void setModel(ApplicationModel newValue) {
        modelProperty().set(newValue);
    }

    /**
     * Exits the application.
     */
    public void exit();

    /**
     * Returns the application node.
     *
     * @return the node
     */
    @Nullable
    default public Node getNode() {
        return null;
    }

    default void addView() {
        createView().thenAccept(this::add);
    }

    /**
     * Creates a new view, initializes it, then invokes the callback.
     *
     * @return A callback.
     */
    CompletionStage<ViewController> createView();

    /**
     * Adds a recent URI.
     *
     * @param uri a recent URI
     * @param dataFormat the data format that was used to access the URI
     */
    default void addRecentURI(URI uri, DataFormat dataFormat) {
        // ensures that the last used uri lands at the end of the LinkedHashSet.
        Set<Map.Entry<URI,DataFormat>> recents = recentUrisProperty().get();
        recents.remove(uri);
        recents.add(new AbstractMap.SimpleEntry<>(uri,dataFormat));
        if (recents.size() > getMaxNumberOfRecentUris()) {
            Iterator<Map.Entry<URI,DataFormat>> i = recents.iterator();
            i.next();
            i.remove();
        }
    }

    default int getMaxNumberOfRecentUris() {
        return maxNumberOfRecentUrisProperty().get();
    }

    default void setMaxNumberOfRecentUris(int newValue) {
        maxNumberOfRecentUrisProperty().set(newValue);
    }
}
