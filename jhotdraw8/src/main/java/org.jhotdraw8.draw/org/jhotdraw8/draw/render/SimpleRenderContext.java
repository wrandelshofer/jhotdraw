/*
 * @(#)SimpleRenderContext.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.render;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Figure;

import java.util.HashMap;
import java.util.Map;

public class SimpleRenderContext implements RenderContext {
    @NonNull
    private Map<Figure, Node> nodeMap = new HashMap<>();
    @NonNull
    private ObservableMap<Key<?>, Object> properties = FXCollections.observableHashMap();

    @Override
    public Node getNode(Figure figure) {
        return nodeMap.computeIfAbsent(figure, f -> f.createNode(this));
    }

    @NonNull
    @Override
    public ObservableMap<Key<?>, Object> getProperties() {
        return properties;
    }
}
