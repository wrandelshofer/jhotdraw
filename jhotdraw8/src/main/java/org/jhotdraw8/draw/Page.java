/* @(#)Page.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.figure.Figure;

/**
 * Defines a page layout for printing.
 * <p>
 * The layout may be used for multiple pages, for example for continuous form
 * paper.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Page extends Figure {

    /**
     * Returns the number of 'internal' pages defined by this page.
     * @return number of internal pages
     */
    int getNumberOfInternalPages();

    /**
     * Returns a node which will be placed on the paper.
     *
     * @param internalPageNumber the internal page number
     * @return a new node
     */
    Node createPageNode(int internalPageNumber);
    
    /**
     * Returns the clip for the page content.
     *
     * @param internalPageNumber the internal page number
     * @return the clipping region
     */
    Shape getPageClip(int internalPageNumber);
    
    /**
     * Returns a transform which will position the drawing contents
     * inside the clip on the page.
     *
     * @param internalPageNumber the internal page number
     * @return the transform
     */
    Transform getPageTransform(int internalPageNumber);
    
    /** Returns the page format.
     * @return  the page format */
    PageFormat getPageFormat();
    
    /**
     * Creates a paper for the specified page.
     *
     * @param internalPageNumber the internal page number
     * @return the internal page number
     */
    Paper createPaper(int internalPageNumber);
    
}
