/* @(#)RenderContext.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.render;

import org.jhotdraw8.draw.figure.Figure;
import javafx.scene.Node;
import javax.annotation.Nonnull;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.draw.figure.Page;

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
    Key<Double> DPI = new ObjectKey<>("dpi", Double.class, 72.0);

    Key<Page> RENDER_PAGE = new ObjectKey<>("renderPage", Page.class, null);
    Key<Integer> RENDER_PAGE_NUMBER = new ObjectKey<>("renderPageNumber", Integer.class, 0);
    Key<Integer> RENDER_NUMBER_OF_PAGES = new ObjectKey<>("renderNumberOfPages", Integer.class, 1);
    Key<Integer> RENDER_PAGE_INTERNAL_NUMBER = new ObjectKey<>("renderPageInternalNumber", Integer.class, 0);
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
    @Nonnull Node getNode(@Nonnull Figure f);

}
