/* @(#)FigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.MapAccessor;

/**
 * FigureKey.
 *
 * @design.pattern org.jhotdraw.draw.model.DrawingModel Strategy, Context.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FigureKey<T> extends Key<T> {
    DirtyMask getDirtyMask();
}
