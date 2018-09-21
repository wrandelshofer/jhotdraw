/* @(#)AbstractActivity.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javax.annotation.Nonnull;
import org.jhotdraw8.collection.HierarchicalMap;
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
 * AbstractActivity.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractActivity extends AbstractDisableable implements ViewController {

    @Nonnull
    protected ObjectProperty<Application> application = new SimpleObjectProperty<>();
    protected final HierarchicalMap<String, Action> actionMap = new HierarchicalMap<>();
    protected final ObservableMap<Key<?>, Object> properties//
            = FXCollections.observableHashMap();
    protected final StringProperty title = new SimpleStringProperty();
    private final IntegerProperty disambiguation = new SimpleIntegerProperty();

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

    protected abstract void initView() ;

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
    public void init() {
        initView();
        initActionMap(actionMap);
                getNode().disableProperty().bind(disabledProperty());
    }
}
