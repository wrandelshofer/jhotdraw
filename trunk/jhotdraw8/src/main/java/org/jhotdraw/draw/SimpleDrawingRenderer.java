/* @(#)SimpleDrawingRenderer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.HashMap;
import javafx.scene.Node;
import org.jhotdraw.beans.SimplePropertyBean;

/**
 * SimpleDrawingRenderer.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class SimpleDrawingRenderer extends SimplePropertyBean implements RenderContext {
    // ---
    // Field declarations
    // ---
    protected final HashMap<Figure,Node> figureToNodeMap = new HashMap<>();
    
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
