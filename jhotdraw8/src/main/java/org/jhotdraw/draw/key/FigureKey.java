/* @(#)FigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.collection.SimpleKey;

/**
 * FigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FigureKey<T> extends MapAccessor<T> {
    DirtyMask getDirtyMask();
}
