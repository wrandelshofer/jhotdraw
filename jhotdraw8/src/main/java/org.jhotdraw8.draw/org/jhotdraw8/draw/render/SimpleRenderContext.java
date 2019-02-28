/* @(#)SimpleRenderContext.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.render;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Figure;

import java.util.HashMap;
import java.util.Map;

public class SimpleRenderContext implements RenderContext {
    private Map<Figure, Node> nodeMap = new HashMap<>();
    private ObservableMap<Key<?>, Object> properties = FXCollections.observableHashMap();

    @Override
    public Node getNode(Figure figure) {
        return nodeMap.computeIfAbsent(figure, f -> f.createNode(this));
    }

    @Override
    public ObservableMap<Key<?>, Object> getProperties() {
        return properties;
    }
}
