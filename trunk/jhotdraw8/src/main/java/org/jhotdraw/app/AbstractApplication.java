/* @(#)AbstractApplication.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app;

import java.net.URI;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jhotdraw.collection.Key;

/**
 * AbstractApplication.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractApplication extends javafx.application.Application implements org.jhotdraw.app.Application {

    /**
     * Holds the recent URIs.
     */
    private final ReadOnlySetProperty<URI> recentUris//
            = new ReadOnlySetWrapper<URI>(//
                    this, RECENT_URIS_PROPERTY, //
                    FXCollections.observableSet(new LinkedHashSet<URI>())).getReadOnlyProperty();

    /**
     * Holds the max number of recent URIs.
     */
    private final IntegerProperty maxNumberOfRecentUris//
            = new SimpleIntegerProperty(//
                    this, MAX_NUMBER_OF_RECENT_URIS_PROPERTY, //
                    10);

    /**
     * Holds the disablers.
     */
    private final SetProperty<Object> disablers = new SimpleSetProperty<>(this, DISABLERS_PROPERTY, FXCollections.observableSet());

    /**
     * True if disablers is not empty.
     */
    private final ReadOnlyBooleanProperty disabled;

    {
        ReadOnlyBooleanWrapper w = new ReadOnlyBooleanWrapper(this, DISABLED_PROPERTY);
        w.bind(Bindings.not(disablers.emptyProperty()));
        disabled = w.getReadOnlyProperty();
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
    public ReadOnlyBooleanProperty disabledProperty() {
        return disabled;
    }

    @Override
    public IntegerProperty maxNumberOfRecentUrisProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SetProperty<Object> disablersProperty() {
        return disablers;
    }

    @Override
    public final ObservableMap<Key<?>, Object> getProperties() {
        if (properties == null) {
            properties = FXCollections.observableMap(new IdentityHashMap<>());
        }
        return properties;
    }

}
