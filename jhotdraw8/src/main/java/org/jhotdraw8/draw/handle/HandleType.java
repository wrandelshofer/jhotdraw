/* @(#)HandleType.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

/**
 * {@code HandleType} is used by tools to request specific handles
 * from figures.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class HandleType {
    /** 
     * Handle of this type should highlight a figure, but should not provide
     * user interaction.
     */
    public final static HandleType SELECT=new HandleType();
    /** 
     * Handles of this type should allow to move (translate) a figure.
     */
    public final static HandleType MOVE=new HandleType();
    /** 
     * Handle of this type should allow to reshape (resize) a figure.
     */
    public final static HandleType RESIZE=new HandleType();
    /** 
     * Handle of this type should allow to transform (scale and rotate) a figure.
     */
    public final static HandleType TRANSFORM=new HandleType();
}
