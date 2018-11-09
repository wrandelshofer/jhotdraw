/* @(#)RenderContext.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.render;

import javafx.geometry.Bounds;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import javafx.scene.Node;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.draw.figure.Page;

import java.time.Instant;

/**
 * RenderContext.
 *
 * @design.pattern RenderContext Builder, Client. The builder pattern is used
 * for the creation of a JavaFX scene graph from a Figure. The creation of the
 * scene graph is delegated to the methods Figure.createNode and
 * Figure.updateNode. Typically each concrete Figure class will generate a
 * different scene graph. The same Figure object may also create different scene
 * graphs depending on property values of the RenderContext. For example a
 * PageFigure will render the current page number of the PrintRenderContext.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface RenderContext extends PropertyBean {

    // ---
    // keys
    // ---
    Key<RenderingIntent> RENDERING_INTENT = new ObjectKey<>("renderingIntent", RenderingIntent.class, RenderingIntent.EDITOR);

    /**
     * The dots per inch of the rendering device.
     */
    Key<Double> DPI = new ObjectKey<>("dpi", Double.class, 96.0);
    /**
     * Contains a non-null value if the rendering is clipped. The clip bounds are given in world coordinates.
     */
    @Nullable
    Key<Bounds> CLIP_BOUNDS = new ObjectKey<>("clipBounds", Bounds.class, null);
    /**
     * Number of nodes that can be rendered per layer in the drawing editor..
     */
    Key<Integer> MAX_NODES_PER_LAYER = new ObjectKey<>("maxNodesPerLayer", Integer.class, 5000);
    
    @Nullable
    Key<Page> RENDER_PAGE = new ObjectKey<>("renderPage", Page.class, null);
    Key<Integer> RENDER_PAGE_NUMBER = new ObjectKey<>("renderPageNumber", Integer.class, 0);
    Key<Integer> RENDER_NUMBER_OF_PAGES = new ObjectKey<>("renderNumberOfPages", Integer.class, 1);
    Key<Integer> RENDER_PAGE_INTERNAL_NUMBER = new ObjectKey<>("renderPageInternalNumber", Integer.class, 0);
    Key<Instant> RENDER_TIMESTAMP = new ObjectKey<>("renderTimestamp", Instant.class, Instant.now());
    // ---
    // behavior
    // ---
    /**
     * Gets the JavaFX node which is used to render the specified figure by this
     * {@code RenderContext}.
     *
     * @param f The figure
     * @return The JavaFX node associated to the figure
     */
     Node getNode( Figure f);

}
