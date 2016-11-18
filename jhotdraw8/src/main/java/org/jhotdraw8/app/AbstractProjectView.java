/* @(#)AbstractProjectView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.app;

import org.jhotdraw8.collection.HierarchicalMap;
import java.net.URI;
import java.util.IdentityHashMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
 */
public abstract class AbstractProjectView extends AbstractDisableable implements ProjectView {

    protected ObjectProperty<Application> application = new SimpleObjectProperty<>();
    protected final HierarchicalMap<String, Action> actionMap = new HierarchicalMap<>();
    /**
     * Disabled is bound to disable. Subclasses can assign a different binding.
     */
    protected final BooleanProperty modified = new SimpleBooleanProperty();
    protected final ObjectProperty<URI> uri = new SimpleObjectProperty<>();
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
    public BooleanProperty modifiedProperty() {
        return modified;
    }

    @Override
    public void clearModified() {
        modified.set(false);
    }

    protected void markAsModified() {
        modified.set(true);
    }

    @Override
    public ObjectProperty<URI> uriProperty() {
        return uri;
    }

    @Override
    public ObjectProperty<Application> applicationProperty() {
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
