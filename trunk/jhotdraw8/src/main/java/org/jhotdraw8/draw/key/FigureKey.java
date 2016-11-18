/* @(#)FigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;

/**
 * FigureKey.
 *
 * @param <T>
 * @design.pattern org.jhotdraw.draw.model.DrawingModel Strategy, Context.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FigureKey<T> extends Key<T> {
    DirtyMask getDirtyMask();
}
