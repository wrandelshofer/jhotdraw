/* @(#)AbstractActivity.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.collection.Key;

/**
 * AbstractActivity.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractActivity extends AbstractDisableable implements Activity {

    @Nonnull
    protected ObjectProperty<Application> application = new SimpleObjectProperty<>(this, APPLICATION_PROPERTY);
    protected final HierarchicalMap<String, Action> actionMap = new HierarchicalMap<>();
    protected final ObservableMap<Key<?>, Object> properties//
            = FXCollections.observableHashMap();
    protected final StringProperty title = new SimpleStringProperty(this, TITLE_PROPERTY);
    private final IntegerProperty disambiguation = new SimpleIntegerProperty(this, DISAMBIGUATION_PROPERTY);

    @Nonnull
    @Override
    public HierarchicalMap<String, Action> getActionMap() {
        return actionMap;
    }

    @Nonnull
    @Override
    public IntegerProperty disambiguationProperty() {
        return disambiguation;
    }

    protected abstract void initActionMap(HierarchicalMap<String, Action> actionMap);

    protected abstract void initView();

    @Nonnull
    @Override
    public StringProperty titleProperty() {
        return title;
    }

    @Nonnull
    @Override
    public ObjectProperty<Application> applicationProperty() {
        return application;
    }

    @Nonnull
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
        initActionMap(actionMap);
        getNode().disableProperty().bind(disabledProperty());
    }
}
