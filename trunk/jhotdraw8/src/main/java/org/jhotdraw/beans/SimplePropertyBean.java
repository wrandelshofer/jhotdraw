/* @(#)SimplePropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.beans;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
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
    /**
     * Holds the properties.
     */
    protected final ReadOnlyMapWrapper<Key<?>, Object> properties = new ReadOnlyMapWrapper<Key<?>, Object>(this, PROPERTIES_PROPERTY, FXCollections.observableHashMap());

    @Override
    public final ReadOnlyMapProperty<Key<?>, Object> properties() {
        return properties.getReadOnlyProperty();
    }
}
