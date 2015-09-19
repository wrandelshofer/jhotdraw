/* @(#)Layer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.draw;

/**
 * Layer.
 * <p>
 * The parent of a {@code Layer} must be a {@code Drawing}.
 * <p>
 * A layer does not have handles.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Layer extends Figure {
    /** Layer figures always return false for isSelectable. */
    @Override
    default public boolean isSelectable() { return false; }
    
}

