/* @(#)SimplePropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jhotdraw8.collection.Key;

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
    protected final ObservableMap<Key<?>, Object> properties = FXCollections.observableHashMap();

    @Override
    public final ObservableMap<Key<?>, Object> getProperties() {
        return properties;
    }
}
