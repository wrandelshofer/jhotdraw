/*
 * @(#)PrintRenderContext.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.render;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.draw.figure.Figure;

/**
 * PrintRenderContext.
 *
 * @author Werner Randelshofer
 */
public interface PrintRenderContext extends RenderContext {

    // ---
    // keys
    // ---
    /**
     * The figure which defines the layout of the current print page. This is
     * typically a {@link org.jhotdraw8.draw.figure.Page}.
     */
    @NonNull
    Key<Figure> PAGE_FIGURE = new ObjectKey<>("pageFigure", Figure.class, null);
    /**
     * Defines the current internal page number of a page figure. A page figure
     * may define the layout for multiple pages - for example for continuous
     * form paper.
     */
    @NonNull
    Key<Integer> INTERNAL_PAGE_NUMBER = new ObjectKey<>("internalPageNumber", Integer.class, null);
    /**
     * Defines the current page number of the print job.
     * <p>
     * This number starts with 1 instead of with 0.
     */
    @NonNull
    Key<Integer> PAGE_NUMBER = new ObjectKey<>("pageNumber", Integer.class, null);
    /**
     * Defines the total number of a pages in the print job.
     */
    @NonNull
    Key<Integer> NUMBER_OF_PAGES = new ObjectKey<>("numberOfPages", Integer.class, null);

}
