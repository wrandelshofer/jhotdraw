/*
 * @(#)AbstractActivity.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.collection.Key;

import java.util.LinkedHashMap;

/**
 * AbstractActivity.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractActivity extends AbstractDisableable implements Activity {

    @NonNull
    protected final ObjectProperty<Application> application = new SimpleObjectProperty<>(this, APPLICATION_PROPERTY);
    protected final ObservableMap<Key<?>, Object> properties//
            = FXCollections.observableHashMap();
    protected final StringProperty title = new SimpleStringProperty(this, TITLE_PROPERTY,
            ApplicationLabels.getResources().getString("unnamedFile"));
    private final IntegerProperty disambiguation = new SimpleIntegerProperty(this, DISAMBIGUATION_PROPERTY);
    private final ReadOnlyMapProperty<String, Action> actions = new ReadOnlyMapWrapper<String, Action>(FXCollections.observableMap(new LinkedHashMap<>())).getReadOnlyProperty();


    public AbstractActivity() {
    }

    @NonNull
    @Override
    public IntegerProperty disambiguationProperty() {
        return disambiguation;
    }

    protected abstract void initActions(@NonNull ObservableMap<String, Action> actionMap);

    protected abstract void initView();

    @NonNull
    @Override
    public StringProperty titleProperty() {
        return title;
    }

    @Override
    public @NonNull ObjectProperty<Application> applicationProperty() {
        return application;
    }

    @NonNull
    @Override
    public ObservableMap<Key<?>, Object> getProperties() {
        return properties;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void start() {
    }

    public void init() {
        initView();
        initTitle();
        initActions(getActions());
        getNode().disableProperty().bind(disabledProperty());
    }

    protected abstract void initTitle();

    @Override
    public @NonNull ReadOnlyMapProperty<String, Action> actionsProperty() {
        return actions;
    }
}
