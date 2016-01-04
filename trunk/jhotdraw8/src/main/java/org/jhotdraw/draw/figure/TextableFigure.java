/* @(#)TextableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.StringStyleableFigureKey;

/**
 * A figure which holds text in an attribute.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface TextableFigure extends Figure {
    
    /** The text. Default value: {@code ""}. */
    public final static StringStyleableFigureKey TEXT = new StringStyleableFigureKey("text", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), "");

}
