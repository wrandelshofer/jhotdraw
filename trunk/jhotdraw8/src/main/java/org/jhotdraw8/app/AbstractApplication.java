/* @(#)AbstractApplication.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.prefs.Preferences;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import static org.jhotdraw8.app.Disableable.DISABLED_PROPERTY;
import org.jhotdraw8.collection.Key;

/**
 * AbstractApplication.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the type of project views that this application manages
 */
public abstract class AbstractApplication<V extends ProjectView<V>> extends javafx.application.Application implements org.jhotdraw8.app.Application<V> {

    /**
     * Holds the max number of recent URIs.
     */
    private final IntegerProperty maxNumberOfRecentUris//
            = new SimpleIntegerProperty(//
                    this, MAX_NUMBER_OF_RECENT_URIS_PROPERTY, //
                    10);

    /**
     * Holds the recent URIs.
     */
    private final ReadOnlySetProperty<URI> recentUris//
            = new ReadOnlySetWrapper<URI>(//
                    this, RECENT_URIS_PROPERTY, //
                    FXCollections.observableSet(new LinkedHashSet<URI>())).getReadOnlyProperty();

   protected void loadRecentUris(String applicationId) {
        Preferences prefs = Preferences.userNodeForPackage(AbstractApplication.class);
        String recentUrisSerialized = prefs.get(applicationId+".recentUris", "");
        for (String str : recentUrisSerialized.split("\t")) {
            if (str.isEmpty()) {
                continue;
            }
            if (recentUris.size() >= getMaxNumberOfRecentUris()) {
                break;
            }
            try {
                URI uri = new URI(str);
                recentUris.add(uri);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
        recentUris.get().addListener((SetChangeListener.Change<? extends URI> change) -> {
            StringBuilder buf = new StringBuilder();
            int skip = recentUris.size() - getMaxNumberOfRecentUris();
            for (URI uri : recentUris) {
                if (--skip > 0) {
                    continue;
                }
                if (buf.length() != 0) {
                    buf.append('\t');
                }
                String str = uri.toString();
                if (str.contains("\t")) {
                    System.err.println("AbstractApplication warning can't store URI in preferences. URI=" + uri);
                    continue;
                }
                buf.append(str);
            }
            prefs.put(applicationId+".recentUris", buf.toString());
        });
    }

  /**
   * Holds the disablers.
   */
  private final ObservableSet<Object> disablers = FXCollections.observableSet();
  /**
   * Holds the disabled state.
   */
  private final ReadOnlyBooleanProperty disabled;

  {
    ReadOnlyBooleanWrapper robw = new ReadOnlyBooleanWrapper(this, DISABLED_PROPERTY);
    robw.bind(Bindings.isNotEmpty(disablers));
    disabled = robw.getReadOnlyProperty();
  }


    @Override
    public ReadOnlyBooleanProperty disabledProperty() {
        return disabled;
    }

   @Override
    public ObservableSet<Object> disablers() {
        return disablers;
    }
    /**
     * Properties.
     */
    private ObservableMap<Key<?>, Object> properties;

    @Override
    public ReadOnlySetProperty<URI> recentUrisProperty() {
        return recentUris;
    }

    @Override
    public IntegerProperty maxNumberOfRecentUrisProperty() {
        return maxNumberOfRecentUris;
    }

    @Override
    public final ObservableMap<Key<?>, Object> getProperties() {
        if (properties == null) {
            properties = FXCollections.observableMap(new IdentityHashMap<>());
        }
        return properties;
    }
    
}
