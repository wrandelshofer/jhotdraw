/*
 * @(#)SimpleDrawingRenderer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.HashMap;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import org.jhotdraw.collection.Key;
import static org.jhotdraw.draw.RenderContext.RENDERING_HINTS_PROPERTY;

/**
 * SimpleDrawingRenderer.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class SimpleDrawingRenderer implements RenderContext {

    /**
     * Holds the rendering hints.
     */
    protected final ReadOnlyMapProperty<Key<?>, Object> renderingHints = new ReadOnlyMapWrapper<Key<?>, Object>(this, RENDERING_HINTS_PROPERTY, FXCollections.observableHashMap()).getReadOnlyProperty();

    protected final HashMap<Figure,Node> figureToNodeMap = new HashMap<>();
    
    @Override
    public ReadOnlyMapProperty<Key<?>, Object> renderingHints() {
        return renderingHints;
    }

    @Override
    public Node getNode(Figure f) {
        Node n = figureToNodeMap.get(f);
        if (n == null) {
            n = f.createNode(this);
            figureToNodeMap.put(f, n);
        }
        return n;
    }
    /** Renders the provided figure into a JavaFX Node.
     * @param figure The figure
     * @return the rendered node
     */
    public Node render(Figure figure) {
        figureToNodeMap.clear();
        renderRecursive(figure);
        return getNode(figure);
    }
    /** Recursive part of the render method.
     * @param figure The figure
     */
    private void renderRecursive(Figure figure) {
        figure.updateNode(this, getNode(figure));
        for (Figure child:figure.children()) {
            renderRecursive(child);
        }
    }
}
