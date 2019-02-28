/* @(#)FigureKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.collection.Key;

/**
 * FigureKey.
 *
 * @param <T> the value type
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern org.jhotdraw8.draw.model.DrawingModel Strategy, Context.
 */
public interface FigureKey<T> extends Key<T> {

    DirtyMask getDirtyMask();
}
