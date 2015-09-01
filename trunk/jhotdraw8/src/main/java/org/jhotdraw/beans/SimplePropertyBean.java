/* @(#)SimplePropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.beans;

import java.util.HashMap;
import javafx.beans.Observable;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jhotdraw.collection.Key;

/**
 * SimplePropertyBean is a simple implementation of the {@code PropertyBean}
 * interface.
 * <p>
 * SimplePropertyBean extends from SimpleObservable and thus fires an invalidation
 * event every time a property is changed.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimplePropertyBean extends SimpleObservable implements PropertyBean {

    /**
     * Holds the properties.
     */
    protected final ReadOnlyMapWrapper<Key<?>, Object> properties = new ReadOnlyMapWrapper<>(this, "properties", FXCollections.observableHashMap());

    @Override
    public final ReadOnlyMapProperty<Key<?>, Object> properties() {
        return properties.getReadOnlyProperty();
    }
}
