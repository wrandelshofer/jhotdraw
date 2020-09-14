/*
 * @(#)Application.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.input.DataFormat;
import javafx.util.Callback;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.beans.NonNullObjectProperty;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.concurrent.FXWorker;
import org.jhotdraw8.util.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

/**
 * An {@code Application} handles the life-cycle of {@link Activity} objects and
 * provides windows to present them on screen.
 *
 * @author Werner Randelshofer
 */
public interface Application extends Disableable, PropertyBean {
    String ACTIONS_PROPERTY = "actions";
    String ACTIVITIES_PROPERTY = "activities";
    String ACTIVITY_FACTORY_PROPERTY = "activityFactory";
    String RESOURCE_BUNDLE_PROPERTY = "resourceBundle";
    String MENU_BAR_FACTORY_PROPERTY = "menuBarFactory";
    String RECENT_URIS_PROPERTY = "recentUris";
    String PREFERENCES_PROPERTY = "preferences";
    String MAX_NUMBER_OF_RECENT_URIS_PROPERTY = "maxNumberOfRecentUris";
    String MODEL_PROPERTY = "model";
    String STYLESHEETS_PROPERTY = "stylesheets";

    Key<String> NAME_KEY = new ObjectKey<>("name", String.class);
    Key<String> VERSION_KEY = new ObjectKey<>("version", String.class);
    Key<String> COPYRIGHT_KEY = new ObjectKey<>("copyright", String.class);
    Key<String> LICENSE_KEY = new ObjectKey<>("license", String.class);

    /**
     * Contains all {@link Activity} objects that are managed by this
     * {@link Application}.
     *
     * @return the activities
     */
    @NonNull ReadOnlySetProperty<Activity> activitiesProperty();

    @NonNull NonNullObjectProperty<Preferences> preferencesProperty();


    /**
     * Contains all {@link Action} objects that are managed by this
     * {@link Application}.
     *
     * @return the activities
     */
    @NonNull ReadOnlyMapProperty<String, Action> actionsProperty();


    @NonNull
    default ObservableMap<String, Action> getActions() {
        return actionsProperty().get();
    }

    @NonNull
    default Preferences getPreferences() {
        return preferencesProperty().get();
    }

    default void setPreferences(@NonNull Preferences preferences) {
        preferencesProperty().set(preferences);
    }

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
    @NonNull
    default ObservableSet<Activity> getActivities() {
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

    /**
     * Creates a new activity, initializes it, then invokes the callback.
     *
     * @return A callback.
     */
    default CompletionStage<Activity> createActivity() {
        return FXWorker.supply(() -> {
            Supplier<Activity> factory = getActivityFactory();
            if (factory == null) {
                throw new IllegalStateException("No activityFactory has been set on the Application.");
            }
            return factory.get();
        });
    }

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

    @NonNull ObjectProperty<Supplier<Activity>> activityFactoryProperty();

    default Supplier<Activity> getActivityFactory() {
        return activityFactoryProperty().get();
    }

    default void setActivityFactory(Supplier<Activity> newValue) {
        activityFactoryProperty().set(newValue);
    }

    @NonNull ObjectProperty<Supplier<MenuBar>> menuBarFactoryProperty();

    @NonNull NonNullObjectProperty<Resources> resourcesProperty();

    @Nullable
    default Supplier<MenuBar> getMenuBarFactory() {
        return menuBarFactoryProperty().get();
    }

    @NonNull ReadOnlyListProperty<String> stylesheetsProperty();

    @NonNull
    default ObservableList<String> getStylesheets() {
        return stylesheetsProperty().get();
    }

    default void setMenuBarFactory(@Nullable Supplier<MenuBar> newValue) {
        menuBarFactoryProperty().set(newValue);
    }

    @NonNull
    default Resources getResources() {
        return resourcesProperty().get();
    }

    default void setResources(@NonNull Resources newValue) {
        resourcesProperty().set(newValue);
    }

    @NonNull
    default <T extends Node> Supplier<T> createFxmlNodeSupplier(@NonNull URL fxml) {
        return createFxmlNodeSupplier(fxml, getResources().asResourceBundle());
    }

    @NonNull
    default <T extends Node> Supplier<T> createFxmlNodeSupplier(@NonNull URL fxml, ResourceBundle resourceBundle) {
        return () -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resourceBundle);
            try (InputStream in = fxml.openStream()) {
                return loader.load(in);
            } catch (IOException ex) {
                throw new InternalError(ex);
            }
        };
    }


    @NonNull
    default <T> Supplier<T> createFxmlControllerSupplier(@NonNull URL fxml,
                                                         @NonNull ResourceBundle resources) {
        return createFxmlControllerSupplier(fxml, resources, (Callback<Class<?>, Object>) null);
    }


    @NonNull
    default <T> Supplier<T> createFxmlControllerSupplier(@NonNull URL fxml,
                                                         @NonNull ResourceBundle resources,
                                                         @Nullable Supplier<T> controllerFactory) {
        return () -> this.<T>createFxmlControllerSupplier(fxml, resources, controllerFactory == null ? null : clazz -> controllerFactory.get()).get();
    }

    @NonNull
    default <T> Supplier<T> createFxmlControllerSupplier(@NonNull URL fxml,
                                                         @NonNull ResourceBundle resources,
                                                         @Nullable Callback<Class<?>, Object> controllerFactory) {
        return () -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setControllerFactory(controllerFactory);
            try (InputStream in = fxml.openStream()) {
                loader.load(in);
                return loader.getController();
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        };
    }

}
