/* @(#)AbstractView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app;

import org.jhotdraw.collection.HierarchicalMap;
import java.net.URI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import org.jhotdraw.app.action.Action;
import org.jhotdraw.collection.Key;

/**
 * AbstractView.
 * @author Werner Randelshofer
 */
public abstract class AbstractView extends AbstractDisableable implements View {
protected ObjectProperty<Application> application = new SimpleObjectProperty<>();
    protected final HierarchicalMap<String, Action> actionMap = new HierarchicalMap<>();
    /** Disabled is bound to disable. Subclasses can assign a different binding. */
    protected final BooleanProperty modified = new SimpleBooleanProperty();
    protected final ObjectProperty<URI> uri = new SimpleObjectProperty<>();
    protected final  MapProperty<Key<?>,  Object> values = new SimpleMapProperty<>(FXCollections.observableHashMap());
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
    
    protected void setModified(boolean newValue) {
        modified.set(newValue);
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
    public MapProperty<Key<?>, Object> properties() {
        return values;
    }

    @Override
    public void dispose() {
    }

}
