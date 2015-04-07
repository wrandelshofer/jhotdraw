/* @(#)AbstractAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action;

import java.util.HashMap;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jhotdraw.app.AbstractDisableable;
import org.jhotdraw.collection.Key;

/**
 * AbstractAction.
 * @author Werner Randelshofer
 */
public abstract class AbstractAction extends AbstractDisableable implements Action {
   private MapProperty<Key<?>, Object> properties;

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
        set(Action.NAME, name);

    }

    @Override
    public final MapProperty<Key<?>, Object> properties() {
        if (properties == null) {
            properties = new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<Key<?>, Object>()));
        }
        return properties;
    }
}
