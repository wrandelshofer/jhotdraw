/*
 * @(#)AbstractCreationTool.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.tool;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ReversedList;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.util.Resources;

import java.util.function.Supplier;

/**
 * AbstractCreationTool.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractCreationTool<F extends Figure> extends AbstractTool {
    protected Supplier<Layer> layerFactory;
    protected Supplier<? extends F> figureFactory;
    /**
     * The created figure.
     */
    @Nullable
    protected F createdFigure;

    public AbstractCreationTool(String name, Resources rsrc, Supplier<? extends F> figureFactory, Supplier<Layer> layerFactory) {
        super(name, rsrc);
        this.figureFactory = figureFactory;
        this.layerFactory = layerFactory;
    }


    public void setFigureFactory(Supplier<F> factory) {
        this.figureFactory = factory;
    }

    public void setLayerFactory(Supplier<Layer> factory) {
        this.layerFactory = factory;
    }

    protected F createFigure() {
        return figureFactory.get();
    }


    /**
     * Finds a layer for the specified figure. Creates a new layer if no
     * suitable layer can be found.
     *
     * @param dv        the drawing view
     * @param newFigure the figure
     * @return a suitable parent for the figure
     */
    @Nullable
    protected Figure getOrCreateParent(@NonNull DrawingView dv, Figure newFigure) {
        // try to use the active layer
        Figure activeParent = dv.getActiveParent();
        if (activeParent != null && activeParent.isEditable() && activeParent.isAllowsChildren()
                && activeParent.isSuitableChild(newFigure)
                && newFigure.isSuitableParent(activeParent)) {
            return activeParent;
        }
        // search for a suitable parent front to back
        Figure layer = null;
        for (Figure candidate : new ReversedList<>(dv.getDrawing().getChildren())) {
            if (candidate.isEditable() && candidate.isAllowsChildren()
                    && newFigure.isSuitableParent(candidate)
                    && candidate.isSuitableChild(newFigure)) {
                layer = candidate;
                break;
            }
        }
        // create a new layer if necessary
        if (layer == null) {
            layer = layerFactory.get();
            dv.getModel().addChildTo(layer, dv.getDrawing());
            if (layer.getParent() != dv.getDrawing()) {
                // the drawing does not accept the layer!
                return dv.getDrawing();
            }
        }
        return layer;
    }
}