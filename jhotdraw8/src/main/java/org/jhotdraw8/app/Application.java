/* @(#)Application.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SetProperty;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.HierarchicalMap;

/**
 * An {@code Application} handles the life-cycle of {@link Activity} objects and
 * provides windows to present them on screen.
 *
 * @design.pattern Application Framework, KeyAbstraction. The application
 * framework supports the creation of document oriented applications which can
 * support platform-specific guidelines. The application framework consists of
 * the following key abstractions: null {@link Application}, {@link ApplicationModel}, {@link Project},
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
     * The set of projects contains all open projects..
     *
     * @return the projects
     */
    public SetProperty<Activity> projectsProperty();

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
    default public ObservableSet<Activity> projects() {
        return projectsProperty().get();
    }

    /**
     * Adds the project to the set of projects and shows it.
     *
     * @param v the view
     */
    default public void add(Activity v) {
        projectsProperty().add(v);
    }

    /**
     * Removes the project from the set of visible projects and hides it.
     *
     * @param v the view
     */
    default public void remove(Activity v) {
        projectsProperty().remove(v);
    }

    /**
     * Provides the currently active project. This is the last project which was
     * focus owner. Returns null, if the application has no projects.
     *
     * @return The active view.
     */
    public ReadOnlyObjectProperty<Activity> activeProjectProperty();

    // Convenience method
    @Nullable
    default public Activity getActiveProject() {
        return activeProjectProperty().get();
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

    default void addActivity() {
        createActivity().thenAccept(this::add);
    }

    /**
     * Creates a new view, initializes it, then invokes the callback.
     *
     * @return A callback.
     */
    CompletionStage<Activity> createActivity();

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

    default void setMaxNumberOfRecentUris(int newValue) {
        maxNumberOfRecentUrisProperty().set(newValue);
    }
}
