/* @(#)SimplePropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.beans;

import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import org.jhotdraw.collection.Key;

/**
 * SimplePropertyBean is a simple implementation of the {@code PropertyBean}
 * interface.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimplePropertyBean implements PropertyBean {

    protected final MapProperty<Key<?>, ObjectProperty<?>> values = new SimpleMapProperty<>(FXCollections.observableHashMap());

    @Override
    public MapProperty<Key<?>, ObjectProperty<?>> valuesProperty() {
        return values;
    }
}
