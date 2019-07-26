/*
 * @(#)AbstractApplication.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
    /**
     * Holds the model.
     */
    @Nullable
    private final ObjectProperty<ApplicationModel> model = new SimpleObjectProperty<>(this, MODEL_PROPERTY, null);

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
    private final ReadOnlySetProperty<Map.Entry<URI, DataFormat>> recentUris//
            = new ReadOnlySetWrapper<Map.Entry<URI, DataFormat>>(//
            this, RECENT_URIS_PROPERTY, //
            FXCollections.observableSet(new LinkedHashSet<Map.Entry<URI, DataFormat>>())).getReadOnlyProperty();

    {
        ReadOnlyBooleanWrapper robw = new ReadOnlyBooleanWrapper(this, DISABLED_PROPERTY);
        robw.bind(Bindings.isNotEmpty(disablers));
        final ReadOnlyBooleanProperty readOnlyProperty = robw.getReadOnlyProperty();
        disabled = readOnlyProperty;
    }

    @Override
    public ReadOnlyBooleanProperty disabledProperty() {
        return disabled;
    }

    @Nonnull
    @Override
    public ObservableSet<Object> disablers() {
        return disablers;
    }

    @Override
    public final ObservableMap<Key<?>, Object> getProperties() {
        if (properties == null) {
            properties = FXCollections.observableHashMap();
        }
        return properties;
    }

    protected void loadRecentUris(String applicationId) {
        Preferences prefs = getModel().getPreferences();
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
                recentUris.add(new AbstractMap.SimpleEntry<>(uri, format));
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
        recentUris.get().addListener((SetChangeListener.Change<? extends Map.Entry<URI, DataFormat>> change) -> {
            StringBuilder buf = new StringBuilder();
            int skip = recentUris.size() - getMaxNumberOfRecentUris();
            for (Map.Entry<URI, DataFormat> entry : recentUris) {
                if (--skip > 0) {
                    continue;
                }
                if (buf.length() != 0) {
                    buf.append('\n');
                }
                URI key = entry.getKey();
                DataFormat value = entry.getValue();
                if (key != null && value != null && value.getIdentifiers() != null && !value.getIdentifiers().isEmpty()) {
                    String str = key.toString() + '\t' + value.getIdentifiers().iterator().next();
                    buf.append(str);
                }
            }
            prefs.put(applicationId + RECENT_URIS, buf.toString());
        });
    }

    @Nonnull
    @Override
    public IntegerProperty maxNumberOfRecentUrisProperty() {
        return maxNumberOfRecentUris;
    }

    @Nullable
    @Override
    public ObjectProperty<ApplicationModel> modelProperty() {
        return model;
    }

    @Override
    public ReadOnlySetProperty<Map.Entry<URI, DataFormat>> recentUrisProperty() {
        return recentUris;
    }

}
