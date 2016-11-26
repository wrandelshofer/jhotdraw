/* @(#)SimpleDrawingRenderer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw;

import java.util.Collection;
import org.jhotdraw8.draw.figure.Figure;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javafx.scene.Group;
import javafx.scene.Node;
import org.jhotdraw8.beans.SimplePropertyBean;
import org.jhotdraw8.collection.Key;

/**
 * SimpleDrawingRenderer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawingRenderer extends SimplePropertyBean implements RenderContext {

    // ---
    // Field declarations
    // ---
    protected final HashMap<Figure, Node> figureToNodeMap = new HashMap<>();

    // ---
    // Behavior
    // ---
    @Override
    public Node getNode(Figure f) {
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
    public Node render(Figure figure) {
        figureToNodeMap.clear();
        renderRecursive(figure);
        return getNode(figure);
    }

    /**
     * Recursive part of the render method.
     *
     * @param figure The figure
     */
    private void renderRecursive(Figure figure) {
        figure.updateNode(this, getNode(figure));
        for (Figure child : figure.getChildren()) {
            renderRecursive(child);
        }
    }

    public static Node toNode(Drawing external, Collection<Figure> selection, Map<Key<?>, Object> renderingHints) {
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
