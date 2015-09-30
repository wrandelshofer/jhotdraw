/* @(#)AbstractView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app;

import org.jhotdraw.collection.HierarchicalMap;
import java.net.URI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import org.jhotdraw.app.action.Action;
import static org.jhotdraw.beans.PropertyBean.PROPERTIES_PROPERTY;
import org.jhotdraw.collection.Key;

/**
 * AbstractView.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractView extends AbstractDisableable implements View {

    protected ObjectProperty<Application> application = new SimpleObjectProperty<>();
    protected final HierarchicalMap<String, Action> actionMap = new HierarchicalMap<>();
    /**
     * Disabled is bound to disable. Subclasses can assign a different binding.
     */
    protected final BooleanProperty modified = new SimpleBooleanProperty();
    protected final ObjectProperty<URI> uri = new SimpleObjectProperty<>();
    protected final ReadOnlyMapProperty<Key<?>, Object> properties//
            = new ReadOnlyMapWrapper<Key<?>, Object>(//
                    this, PROPERTIES_PROPERTY, //
                    FXCollections.observableHashMap()).getReadOnlyProperty();
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
    public ReadOnlyMapProperty<Key<?>, Object> propertiesProperty() {
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
