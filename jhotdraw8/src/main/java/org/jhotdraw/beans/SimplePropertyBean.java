/* @(#)SimplePropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
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
    protected final ReadOnlyMapProperty<Key<?>, Object> properties = new ReadOnlyMapWrapper<Key<?>, Object>(this, PROPERTIES_PROPERTY, FXCollections.observableHashMap()).getReadOnlyProperty();

    @Override
    public final ReadOnlyMapProperty<Key<?>, Object> properties() {
        return properties;
    }
}
