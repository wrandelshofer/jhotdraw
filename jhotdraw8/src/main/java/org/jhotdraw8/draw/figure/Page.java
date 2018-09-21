/* @(#)Page.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.lang.reflect.Field;
import javafx.geometry.Bounds;
import javafx.print.Paper;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.text.CssSize2D;

/**
 * Defines a page layout for printing.
 * <p>
 * The layout may be used for multiple pages, for example for continuous form
 * paper.
 * <p>
 * The parent of a page must be a {@link Layer}. A page may have children.
 *
 * @author Werner Randelshofer
 * @version $Id$
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
   * Creates a paper for the specified page.
   *
   * @param internalPageNumber the internal page number
   * @return the internal page number
   */
    default Paper createPaper(int internalPageNumber) {
    CssSize2D size = getPaperSize();
    double w = DefaultUnitConverter.getInstance().convert(size.getX(), "pt");
    double h = DefaultUnitConverter.getInstance().convert(size.getY(), "pt");
    for (Field f : Paper.class.getDeclaredFields()) {
      if (f.isAccessible() && f.getType() == Paper.class) {
        try {
          Paper p = (Paper) f.get(null);
          if ((Math.abs(p.getWidth() - w) < 1 && Math.abs(p.getHeight() - h) < 1)
                  || (Math.abs(p.getHeight() - w) < 1 && Math.abs(p.getWidth() - h) < 1)) {
            return p;
          }
        } catch (@Nonnull IllegalArgumentException | IllegalAccessException ex) {
          // continue
        }
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
   CssSize2D getPaperSize();

  @Override
  default boolean isAllowsChildren() {
    return true;
  }

  @Override
  default boolean isSuitableParent( Figure newParent) {
    return (newParent instanceof Layer) || (newParent instanceof Clipping);
  }

}
