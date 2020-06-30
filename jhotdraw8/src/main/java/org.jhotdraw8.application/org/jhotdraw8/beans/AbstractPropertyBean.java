/*
 * @(#)SimplePropertyBean.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;

import java.util.LinkedHashMap;

/**
 * An abstrac implementation of the {@link PropertyBean} interface.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractPropertyBean implements PropertyBean {

    /**
     * Holds the properties.
     */
    protected final ObservableMap<Key<?>, Object> properties = FXCollections.observableMap(new LinkedHashMap<>());

    @NonNull
    @Override
    public final ObservableMap<Key<?>, Object> getProperties() {
        return properties;
    }
}
