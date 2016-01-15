/* @(#)PrintRenderContext.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw;

import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.SimpleKey;
import org.jhotdraw.draw.figure.Figure;

/**
 * PrintRenderContext.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface PrintRenderContext extends RenderContext {
    // ---
    // keys
    // ---
    /** The figure which defines the layout of the current print page.
     *  This is typically a {@link org.jhotdraw.draw.figure.PageFigure}.
     */
    Key<Figure> PAGE_FIGURE = new SimpleKey<>("pageFigure",Figure.class,null);
    /** Defines the current internal page number of a page figure.
     * A page figure may define the layout for multiple pages - for example for
     * continuous form paper.
     */
    Key<Integer> INTERNAL_PAGE_NUMBER = new SimpleKey<>("internalPageNumber",Integer.class,null);
    /** Defines the current page number of the print job.
     * <p>
     * This number starts with 1 instead of with 0. 
     */
    Key<Integer> PAGE_NUMBER = new SimpleKey<>("pageNumber",Integer.class,null);
    /** Defines the total number of a pages in the print job.
     */
    Key<Integer> NUMBER_OF_PAGES = new SimpleKey<>("numberOfPages",Integer.class,null);

}
