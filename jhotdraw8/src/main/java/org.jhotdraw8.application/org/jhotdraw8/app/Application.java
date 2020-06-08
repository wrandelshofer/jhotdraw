/*
 * @(#)Application.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SetProperty;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.HierarchicalMap;

import java.net.URI;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;

/**
 * An {@code Application} handles the life-cycle of {@link Activity} objects and
 * provides windows to present them on screen.
 *
 * @author Werner Randelshofer
 * @design.pattern Application Framework, KeyAbstraction. The application
 * framework supports the creation of document-based applications which can
 * support platform-specific guidelines. The application framework consists of
 * the following key abstractions: {@link Application}, {@link ApplicationModel}, {@link Activity},
 * {@link Action}.
 */
public interface Application extends Disableable, PropertyBean {

    String RECENT_URIS_PROPERTY = "recentUris";
    String MAX_NUMBER_OF_RECENT_URIS_PROPERTY = "maxNumberOfRecentUris";
    String MODEL_PROPERTY = "model";

    /**
     * The application model.
     *
     * @return the model
     */
    @Nullable ObjectProperty<ApplicationModel> modelProperty();

    /**
     * The list of activities contains all open activities.
     * <p>
     * Altough this is a list, an activity may only by contained
     * once.
     *
     * @return the activities
     */
    @NonNull SetProperty<Activity> activitiesProperty();

    /**
     * The set of recent URIs. The set must be ordered by most recently used
     * first. Only the first items as specified in
     * {@link #maxNumberOfRecentUrisProperty} of the set are used and persisted
     * in user preferences.
     *
     * @return the recent Uris
     */
    ReadOnlySetProperty<Map.Entry<URI, DataFormat>> recentUrisProperty();

    /**
     * The maximal number of recent URIs. Specifies how many items of
     * {@link #recentUrisProperty} are used and persisted in user preferences.
     * This number is also persisted.
     *
     * @return the number of recent Uris
     */
    @NonNull IntegerProperty maxNumberOfRecentUrisProperty();

    // Convenience method
    default ObservableSet<Activity> activities() {
        return activitiesProperty().get();
    }

    /**
     * Adds the activity to the set of activities and shows it.
     *
     * @param v the activity
     */
    default void add(Activity v) {
        activitiesProperty().add(v);
    }

    /**
     * Removes the activities from the set of visible views and hides it.
     *
     * @param v the activities
     */
    default void remove(Activity v) {
        activitiesProperty().remove(v);
    }

    /**
     * Provides the currently active activities. This is the last activities which was
     * focus owner. Returns null, if the application has no views.
     *
     * @return The active activities.
     */
    ReadOnlyObjectProperty<Activity> activeActivityProperty();

    // Convenience method
    @Nullable
    default Activity getActiveActivity() {
        return activeActivityProperty().get();
    }

    /**
     * Returns the action map of the application.
     *
     * @return the action map
     */
    HierarchicalMap<String, Action> getActionMap();

    /**
     * Executes a worker on the thread pool of the application.
     *
     * @param r the runnable
     */
    void execute(Runnable r);

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
    void exit();

    /**
     * Returns the application node.
     *
     * @return the node
     */
    @Nullable
    default Node getNode() {
        return null;
    }

    default void addActivity() {
        createActivity().thenAccept(this::add);
    }

    /**
     * Creates a new activity, initializes it, then invokes the callback.
     *
     * @return A callback.
     */
    CompletionStage<Activity> createActivity();

    /**
     * Adds a recent URI.
     *
     * @param uri        a recent URI
     * @param dataFormat the data format that was used to access the URI
     */
    default void addRecentURI(URI uri, DataFormat dataFormat) {
        // ensures that the last used uri lands at the end of the LinkedHashSet.
        Set<Map.Entry<URI, DataFormat>> recents = recentUrisProperty().get();
        AbstractMap.SimpleEntry<URI, DataFormat> entry = new AbstractMap.SimpleEntry<>(uri, dataFormat);
        recents.remove(entry);
        recents.add(entry);
        if (recents.size() > getMaxNumberOfRecentUris()) {
            Iterator<Map.Entry<URI, DataFormat>> i = recents.iterator();
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
