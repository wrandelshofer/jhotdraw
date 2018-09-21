/* @(#)ConcatenatedPathIterator.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.awt.geom.PathIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import javafx.scene.shape.FillRule;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Concatenates multiple path iterators.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ConcatenatedPathIterator implements PathIterator {

    @Nullable
    private PathIterator current;
    private Deque<PathIterator> iterators;
    private final int windingRule;

    public ConcatenatedPathIterator(FillRule fillRule, @Nonnull List<PathIterator> iteratorList) {
        this(fillRule == FillRule.EVEN_ODD ? WIND_EVEN_ODD : WIND_NON_ZERO, iteratorList);
    }

    public ConcatenatedPathIterator(int windingRule, @Nonnull List<PathIterator> iteratorList) {
        this.windingRule = windingRule;
        this.iterators = new ArrayDeque<>(iteratorList);
        current = iteratorList.isEmpty() ? null : this.iterators.removeFirst();
    }

    @Override
    public int currentSegment(float[] coords) {
        return current.currentSegment(coords);
    }

    @Override
    public int currentSegment(double[] coords) {
        return current.currentSegment(coords);
    }

    @Override
    public int getWindingRule() {
        return windingRule;
    }

    @Override
    public boolean isDone() {
        while (current != null && current.isDone()) {
            current = iterators.isEmpty() ? null : iterators.removeFirst();
        }
        return current == null;
    }

    @Override
    public void next() {
        if (!isDone()) {
            current.next();
        }
    }
}
