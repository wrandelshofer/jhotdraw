/*
 * @(#)AbstractActivity.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
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
    protected final ReadOnlyObjectProperty<Application> application;
    protected final ObservableMap<Key<?>, Object> properties//
            = FXCollections.observableHashMap();
    protected final StringProperty title = new SimpleStringProperty(this, TITLE_PROPERTY);
    private final IntegerProperty disambiguation = new SimpleIntegerProperty(this, DISAMBIGUATION_PROPERTY);
    private final ReadOnlyMapProperty<String, Action> actions = new ReadOnlyMapWrapper<String, Action>(FXCollections.observableMap(new LinkedHashMap<>())).getReadOnlyProperty();


    public AbstractActivity(@NonNull Application application) {
        this.application = new ReadOnlyObjectWrapper<>(this, APPLICATION_PROPERTY, application);
    }

    @NonNull
    @Override
    public IntegerProperty disambiguationProperty() {
        return disambiguation;
    }

    protected abstract void initActions(ObservableMap<String, Action> actionMap);

    protected abstract void initView();

    @NonNull
    @Override
    public StringProperty titleProperty() {
        return title;
    }

    @NonNull
    @Override
    public ReadOnlyObjectProperty<Application> applicationProperty() {
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
        initActions(getActions());
        getNode().disableProperty().bind(disabledProperty());
    }

    @Override
    public @NonNull ReadOnlyMapProperty<String, Action> actionsProperty() {
        return actions;
    }
}
