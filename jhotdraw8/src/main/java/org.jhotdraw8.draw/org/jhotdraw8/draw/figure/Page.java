/*
 * @(#)Page.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Bounds;
import javafx.print.Paper;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.css.UnitConverter;

import java.util.Arrays;
import java.util.List;

/**
 * Defines a page layout for printing.
 * <p>
 * The layout may be used for multiple pages, for example for continuous form
 * paper.
 * <p>
 * The parent of a page must be a {@link Layer}. A page may have children.
 *
 * @author Werner Randelshofer
 */
public interface Page extends Figure {

    /**
     * Returns a node which will be placed on the paper.
     *
     * @param internalPageNumber the internal page number
     * @return a new node
     */
    Node createPageNode(int internalPageNumber);

    /**
     * List of all available papers.
     */
    List<Paper> PAPERS = Arrays.asList(
            Paper.A0,
            Paper.A1,
            Paper.A2,
            Paper.A3,
            Paper.A4,
            Paper.A5,
            Paper.A6,
            Paper.DESIGNATED_LONG,
            Paper.NA_LETTER,
            Paper.LEGAL,
            Paper.TABLOID,
            Paper.EXECUTIVE,
            Paper.NA_8X10,
            Paper.MONARCH_ENVELOPE,
            Paper.NA_NUMBER_10_ENVELOPE,
            Paper.C,
            Paper.JIS_B4,
            Paper.JIS_B5,
            Paper.JIS_B6,
            Paper.JAPANESE_POSTCARD
    );

    /**
     * Creates a paper for the specified page.
     *
     * @param internalPageNumber the internal page number
     * @return the internal page number
     */
    default Paper createPaper(int internalPageNumber) {
        CssPoint2D size = getPaperSize();
        UnitConverter c = new DefaultUnitConverter(72);
        double w = c.convert(size.getX(), "pt");
        double h = c.convert(size.getY(), "pt");

        for (Paper p : PAPERS) {
            if (p.getWidth() == w && p.getHeight() == h
                    || p.getHeight() == w && p.getWidth() == h
            ) {
                return p;
            }
        }

        return Paper.A4;
    }

    /**
     * Returns the number of sub-pages defined by this page.
     *
     * @return number of internal pages
     */
    int getNumberOfSubPages();

    /**
     * Returns the bounds for the page content.
     *
     * @param internalPageNumber the internal page number
     * @return the clipping region
     */
    Bounds getPageBounds(int internalPageNumber);

    /**
     * Returns the clip for the page content.
     *
     * @param internalPageNumber the internal page number
     * @return the clipping region
     */
    Shape getPageClip(int internalPageNumber);

    /**
     * Returns a transform which will position the drawing contents inside the
     * clip on the page.
     *
     * @param internalPageNumber the internal page number
     * @return the transform
     */
    Transform getPageTransform(int internalPageNumber);

    /**
     * Returns the paper size.
     *
     * @return the page size
     */
    CssPoint2D getPaperSize();

    @Override
    default boolean isAllowsChildren() {
        return true;
    }

    @Override
    default boolean isSuitableParent(Figure newParent) {
        return (newParent instanceof Layer) || (newParent instanceof Clipping);
    }

}
