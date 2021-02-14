/*
 * @(#)AbstractApplication.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.scene.control.MenuBar;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.beans.NonNullObjectProperty;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.util.EmptyResources;
import org.jhotdraw8.util.Resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

/**
 * AbstractApplication.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractApplication extends javafx.application.Application implements org.jhotdraw8.app.Application {

    private static final String RECENT_URIS = ".recentUriFormats";

    /**
     * Holds the disabled state.
     */
    private final ReadOnlyBooleanProperty disabled;
    /**
     * Holds the disablers.
     */
    private final ObservableSet<Object> disablers = FXCollections.observableSet();

    private final ReadOnlyMapProperty<String, Action> actions = new ReadOnlyMapWrapper<String, Action>(this, ACTIONS_PROPERTY, FXCollections.observableMap(new LinkedHashMap<>())).getReadOnlyProperty();
    private final ReadOnlySetProperty<Activity> activities = new ReadOnlySetWrapper<Activity>(this, ACTIVITIES_PROPERTY, FXCollections.observableSet(new LinkedHashSet<>())).getReadOnlyProperty();
    private final ReadOnlyListProperty<String> stylesheets = new javafx.beans.property.ReadOnlyListWrapper<String>(this, STYLESHEETS_PROPERTY, FXCollections.observableArrayList()).getReadOnlyProperty();

    private final ObjectProperty<Supplier<Activity>> activityFactory = new SimpleObjectProperty<>(this, ACTIVITY_FACTORY_PROPERTY);
    private final ObjectProperty<Supplier<MenuBar>> menuFactory = new SimpleObjectProperty<>(this, MENU_BAR_FACTORY_PROPERTY);
    private final ObjectProperty<Supplier<URIChooser>> openChooserFactory = new SimpleObjectProperty<>(this, ACTIVITY_FACTORY_PROPERTY);
    private final NonNullObjectProperty<Resources> resources = new NonNullObjectProperty<>(this, RESOURCE_BUNDLE_PROPERTY, new EmptyResources());
    private final NonNullObjectProperty<Preferences> preferences = new NonNullObjectProperty<>(this, PREFERENCES_PROPERTY, Preferences.userNodeForPackage(getClass()));

    /**
     * Holds the max number of recent URIs.
     */
    private final IntegerProperty maxNumberOfRecentUris//
            = new SimpleIntegerProperty(//
            this, MAX_NUMBER_OF_RECENT_URIS_PROPERTY, //
            10);
    /**
     * Properties.
     */
    private ObservableMap<Key<?>, Object> properties;

    /**
     * Holds the recent URIs.
     */
    private final ReadOnlyMapProperty<URI, DataFormat> recentUris//
            = new ReadOnlyMapWrapper<URI, DataFormat>(//
            this, RECENT_URIS_PROPERTY, //
            FXCollections.observableMap(new LinkedHashMap<URI, DataFormat>(16, 0.5f, true))).getReadOnlyProperty();

    {// initializer for 'disabled' property
        ReadOnlyBooleanWrapper robw = new ReadOnlyBooleanWrapper(this, DISABLED_PROPERTY);
        robw.bind(Bindings.isNotEmpty(disablers));
        final ReadOnlyBooleanProperty readOnlyProperty = robw.getReadOnlyProperty();
        disabled = readOnlyProperty;
    }

    @Override
    public ReadOnlyBooleanProperty disabledProperty() {
        return disabled;
    }

    @Override
    public @NonNull ObservableSet<Object> disablers() {
        return disablers;
    }

    @Override
    public final @NonNull ObservableMap<Key<?>, Object> getProperties() {
        if (properties == null) {
            properties = FXCollections.observableHashMap();
        }
        return properties;
    }

    protected void loadRecentUris(String applicationId) {
        Preferences prefs = getPreferences();
        String recentUrisSerialized = prefs.get(applicationId + RECENT_URIS, "");
        for (String row : recentUrisSerialized.split("\n")) {
            if (row.isEmpty()) {
                continue;
            }
            if (recentUris.size() >= getMaxNumberOfRecentUris()) {
                break;
            }
            String[] columns = row.split("\t");
            if (columns.length < 1) {
                continue;
            }
            try {
                URI uri = new URI(columns[0]);
                DataFormat format = null;
                if (columns.length > 1 && !columns[1].isEmpty()) {
                    format = DataFormat.lookupMimeType(columns[1]);
                    if (format == null) {
                        format = new DataFormat(columns[1]);
                    }
                }
                recentUris.put(uri, format);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
        recentUris.get().addListener((MapChangeListener.Change<? extends URI, ? extends DataFormat> change) -> {
            StringBuilder buf = new StringBuilder();
            int skip = recentUris.size() - getMaxNumberOfRecentUris();
            for (Map.Entry<URI, DataFormat> entry : recentUris.entrySet()) {
                if (skip-- > 0) {
                    continue;
                }
                if (buf.length() != 0) {
                    buf.append('\n');
                }
                URI uri = entry.getKey();
                DataFormat format = entry.getValue();
                if (uri != null) {
                    buf.append(uri.toString());
                    if (format != null && format.getIdentifiers() != null && !format.getIdentifiers().isEmpty()) {
                        buf.append('\t').append(format.getIdentifiers().iterator().next());
                    }
                }
            }
            prefs.put(applicationId + RECENT_URIS, buf.toString());
        });
    }

    @Override
    public @NonNull IntegerProperty maxNumberOfRecentUrisProperty() {
        return maxNumberOfRecentUris;
    }

    @Override
    public ReadOnlyMapProperty<URI, DataFormat> recentUrisProperty() {
        return recentUris;
    }

    @Override
    public @NonNull ReadOnlyMapProperty<String, Action> actionsProperty() {
        return actions;
    }

    @Override
    public @NonNull ReadOnlySetProperty<Activity> activitiesProperty() {
        return activities;
    }

    @Override
    public @NonNull ReadOnlyListProperty<String> stylesheetsProperty() {
        return stylesheets;
    }

    @Override
    public @NonNull ObjectProperty<Supplier<Activity>> activityFactoryProperty() {
        return activityFactory;
    }

    @Override
    public @NonNull ObjectProperty<Supplier<MenuBar>> menuBarFactoryProperty() {
        return menuFactory;
    }

    @Override
    public @NonNull NonNullObjectProperty<Resources> resourcesProperty() {
        return resources;
    }

    @Override
    public @NonNull NonNullObjectProperty<Preferences> preferencesProperty() {
        return preferences;
    }
}

