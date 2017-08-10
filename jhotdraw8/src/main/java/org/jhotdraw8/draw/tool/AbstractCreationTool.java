/* @(#)AbstractCreationTool.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.tool;

import java.util.function.Supplier;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.util.ReversedList;

/**
 * AbstractCreationTool.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractCreationTool<F extends Figure> extends AbstractTool {
    protected Supplier<Layer> layerFactory;
    protected Supplier<F> figureFactory;
    /**
     * The created figure.
     */
    protected F createdFigure;
    
    public AbstractCreationTool(String name, Resources rsrc, Supplier<F> figureFactory, Supplier<Layer> layerFactory) {
        super(name, rsrc);
        this.figureFactory=figureFactory;
        this.layerFactory=layerFactory;
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
     * @param dv the drawing view
     * @param newFigure the figure
     * @return a suitable layer for the figure
     */
    protected Layer getOrCreateLayer(DrawingView dv, Figure newFigure) {
        // try to use the active layer
        Layer activeLayer = dv.getActiveLayer();
        if (activeLayer != null && activeLayer.isEditable() && activeLayer.isAllowsChildren()) {
            return activeLayer;
        }
        // search for a suitable layer front to back
        Layer layer = null;
        for (Figure candidate : new ReversedList<>(dv.getDrawing().getChildren())) {
            if (candidate.isEditable() && candidate.isAllowsChildren()) {
                layer = (Layer) candidate;
                break;
            }
        }
        // create a new layer if necessary
        if (layer == null) {
            layer = layerFactory.get();
            dv.getModel().addChildTo(layer, dv.getDrawing());
        }
        return layer;
    }
}