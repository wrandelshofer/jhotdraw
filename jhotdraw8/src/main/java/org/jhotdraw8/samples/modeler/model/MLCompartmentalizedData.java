package org.jhotdraw8.samples.modeler.model;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableMap;

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
    @Nonnull
    private final ImmutableMap<String, ImmutableList<String>> map;

    public MLCompartmentalizedData() {
        this.map = ImmutableMap.emptyMap();
    }

    public MLCompartmentalizedData(ImmutableMap<String, ImmutableList<String>> map) {
        this.map = map;
    }

    public MLCompartmentalizedData(Map<String, ? extends ImmutableList<String>> map) {
        this.map = ImmutableMap.ofMap(map);
    }

    @Nonnull
    public ImmutableMap<String, ImmutableList<String>> getMap() {
        return map;
    }

    @Override
    public boolean equals(Object o) {
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

    @Override
    public String toString() {
        return "MLCompartmentalizedData{" +
                map +
                '}';
    }
}
