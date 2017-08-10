/* @(#)CombinedPathIterator.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.awt.geom.PathIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import javafx.scene.shape.FillRule;

/**
 * CombinedPathIterator.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CombinedPathIterator implements PathIterator {

    private PathIterator current;
    private Deque<PathIterator> iterators;
    private final int windingRule;

    public CombinedPathIterator(FillRule fillRule, List<PathIterator> iteratorList) {
        this(fillRule == FillRule.EVEN_ODD ? WIND_EVEN_ODD : WIND_NON_ZERO, iteratorList);
    }

    public CombinedPathIterator(int windingRule, List<PathIterator> iteratorList) {
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
