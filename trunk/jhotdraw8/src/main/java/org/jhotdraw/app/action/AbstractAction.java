/* @(#)AbstractAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import org.jhotdraw.app.AbstractDisableable;
import org.jhotdraw.collection.Key;

/**
 * AbstractAction.
 * @author Werner Randelshofer
 */
public abstract class AbstractAction extends AbstractDisableable implements Action {

    protected final  MapProperty<Key<?>,  ObjectProperty<?>> values = new SimpleMapProperty<>(FXCollections.observableHashMap());

    /** Creates a new instance.
     * Binds {@code disabled} to {@code disable}.
     */
    public AbstractAction() {
this(null);

    }
    /** Creates a new instance.
     * Binds {@code disabled} to {@code disable}.
     * @param name the name of the action
     */
    public AbstractAction(String name) {
        putValue(Action.NAME,name);

    }

    @Override
    public MapProperty<Key<?>, ObjectProperty<?>> valuesProperty() {
        return values;
    }
}
