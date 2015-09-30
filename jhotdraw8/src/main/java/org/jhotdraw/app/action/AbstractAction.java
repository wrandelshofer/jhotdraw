/* @(#)AbstractAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app.action;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import org.jhotdraw.app.AbstractDisableable;
import org.jhotdraw.collection.Key;

/**
 * AbstractAction.
 * @author Werner Randelshofer
 */
public abstract class AbstractAction extends AbstractDisableable implements Action {
    /**
     * Holds the properties.
     */
    protected final ReadOnlyMapProperty<Key<?>, Object> properties//
            = new ReadOnlyMapWrapper<Key<?>, Object>(//
                    this, PROPERTIES_PROPERTY, //
                    FXCollections.observableHashMap()).getReadOnlyProperty();

    /** Creates a new instance.
     * Binds {@code disabled} to {@code disable}.
     */
    public AbstractAction() {
        this(null);

    }

    /** Creates a new instance.
     * Binds {@code disabled} to {@code disable}.
     * @param id the id of the action
     */
    public AbstractAction(String id) {
        set(Action.NAME, id);

    }

    @Override
    public final ReadOnlyMapProperty<Key<?>, Object> propertiesProperty() {
        return properties;
    }
}
