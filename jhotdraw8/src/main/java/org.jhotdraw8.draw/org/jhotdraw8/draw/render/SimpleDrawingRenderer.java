/*
 * @(#)SimpleDrawingRenderer.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.render;

import javafx.scene.Group;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.AbstractPropertyBean;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * SimpleDrawingRenderer.
 *
 * @author Werner Randelshofer
 */
public class SimpleDrawingRenderer extends AbstractPropertyBean implements RenderContext {

    // ---
    // Field declarations
    // ---
    protected final Map<Figure, Node> figureToNodeMap = new HashMap<>();


    // ---
    // Behavior
    // ---
    @Override
    public Node getNode(@NonNull Figure f) {
        Node n = figureToNodeMap.get(f);
        if (n == null) {
            n = f.createNode(this);
            figureToNodeMap.put(f, n);
        }
        return n;
    }

    /**
     * Renders the provided figure into a JavaFX Node.
     *
     * @param figure The figure
     * @return the rendered node
     */
    public Node render(@NonNull Figure figure) {
        figureToNodeMap.clear();
        renderRecursive(figure);
        return getNode(figure);
    }

    /**
     * Recursive part of the render method.
     *
     * @param figure The figure
     */
    private void renderRecursive(@NonNull Figure figure) {
        figure.updateNode(this, getNode(figure));
        for (Figure child : figure.getChildren()) {
            renderRecursive(child);
        }
    }

    public static Node toNode(@NonNull Drawing external, @NonNull Collection<Figure> selection, @Nullable Map<Key<?>, Object> renderingHints) {
        SimpleDrawingRenderer r = new SimpleDrawingRenderer();
        if (renderingHints != null) {
            r.getProperties().putAll(renderingHints);
        }
        LinkedList<Node> nodes = new LinkedList<>();
        for (Figure f : external.preorderIterable()) {
            if (selection.contains(f)) {
                nodes.add(r.render(f));
            }
        }
        Node drawingNode;
        if (nodes.size() == 1) {
            drawingNode = nodes.getFirst();
        } else {
            drawingNode = new Group(nodes);
        }
        return drawingNode;
    }

}
