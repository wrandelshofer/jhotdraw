/*
 * @(#)BezierPathOutlineHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import org.jhotdraw8.geom.BezierNode;

public class BezierPathOutlineHandle extends PathIterableOutlineHandle {
    final private MapAccessor<ImmutableList<BezierNode>> pointKey;

    public BezierPathOutlineHandle(PathIterableFigure figure, MapAccessor<ImmutableList<BezierNode>> pointKey) {
        super(figure, true);
        this.pointKey = pointKey;
    }
}
