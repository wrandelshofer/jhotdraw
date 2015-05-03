/* @(#)HandleLevel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.draw.handle;

/**
 * HandleLevel.
 * @author Werner Randelshofer
 * @version $Id$
 */
public enum HandleLevel {
    /** A highlight handle is used to highlight a figure.
     * Highlight handles are not interactive.  
     * They are typically used to show which figures are part
     * of a composite figure.
     */
    HIGHLIGHT,
    
    /** A shape handle is used to edit the overall shape of a figure. */
   SHAPE,
   
    /** A transform handle is used to edit a transform of a figure. */
   TRANSFORM, 
   
    /** A point handle is used to edit an individual point of a figure. */
   POINT
}
