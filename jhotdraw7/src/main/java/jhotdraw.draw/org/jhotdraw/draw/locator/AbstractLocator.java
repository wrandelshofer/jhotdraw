/* @(#)AbstractLocator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.locator;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.xml.DOMStorable;

import java.awt.geom.Point2D;
import java.io.Serializable;
/**
 * This abstract class can be extended to implement a {@link Locator}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractLocator implements Locator, DOMStorable, Serializable {
    private static final long serialVersionUID = 1L;
    
    /** Creates a new instance. */
    public AbstractLocator() {
    }
    
    @Override
    public Point2D.Double locate(Figure owner, Figure dependent) {
        return locate(owner);
    }
    
    
}
