/*
 * @(#)MLCompartmentalizedData.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.model;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableMap;
import org.jhotdraw8.collection.ImmutableMaps;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a set of compartmentalized data.
 * <p>
 * Each compartment has a label and a list of textual items.
 * <p>
 * Thus this is effectively a map of type {@literal Map<String,List<String>>>}.
 */
public class MLCompartmentalizedData {
    @NonNull
    private final ImmutableMap<String, ImmutableList<String>> map;

    public MLCompartmentalizedData() {
        this.map = ImmutableMaps.emptyMap();
    }

    public MLCompartmentalizedData(@NonNull ImmutableMap<String, ImmutableList<String>> map) {
        this.map = map;
    }

    public MLCompartmentalizedData(@NonNull Map<String, ? extends ImmutableList<String>> map) {
        this.map = ImmutableMaps.ofMap(map);
    }

    @NonNull
    public ImmutableMap<String, ImmutableList<String>> getMap() {
        return map;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MLCompartmentalizedData that = (MLCompartmentalizedData) o;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    @NonNull
    @Override
    public String toString() {
        return "MLCompartmentalizedData{" +
                map +
                '}';
    }
}
