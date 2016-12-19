/* @(#)AbstractProjectView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app;

import org.jhotdraw8.collection.HierarchicalMap;
import java.util.IdentityHashMap;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.collection.Key;

/**
 * AbstractProjectView.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the project view type
 */
public abstract class AbstractProjectView<V extends ProjectView<V >> extends AbstractDisableable implements ProjectView<V> {

    protected ObjectProperty<Application<V>> application = new SimpleObjectProperty<>();
    protected final HierarchicalMap<String, Action> actionMap = new HierarchicalMap<>();
    protected final ObservableMap<Key<?>, Object> properties//
            =                  FXCollections.observableMap(new IdentityHashMap<>());
    protected final StringProperty title = new SimpleStringProperty();
    private final IntegerProperty disambiguation = new SimpleIntegerProperty();

    @Override
    public HierarchicalMap<String, Action> getActionMap() {
        return actionMap;
    }

    @Override
    public IntegerProperty disambiguationProperty() {
        return disambiguation;
    }

    @Override
    public StringProperty titleProperty() {
        return title;
    }


    @Override
    public ObjectProperty<Application<V>> applicationProperty() {
        return application;
    }

    @Override
    public ObservableMap<Key<?>, Object> getProperties() {
        return properties;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void deactivate() {
    }

    @Override
    public void activate() {
    }

    @Override
    public void start() {
    }

}
