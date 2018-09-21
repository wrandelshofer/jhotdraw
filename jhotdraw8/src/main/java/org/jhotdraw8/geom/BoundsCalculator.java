/* @(#)BoundsCalculator.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.util.stream.Collector;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javax.annotation.Nonnull;

/**
 * A state object for finding the combined bounds of a stream of Bounds objects.
 * <p>
 * This class is designed to work with a (parallel) stream of Bounds objects with:
 * <pre>{@code
 * Stream<Bounds> boundsStream = ... ;
 * BoundsCalculator stats = boundsStream.collect(BoundsCalculator::new,
 *                                                    BoundsCalculator::accept,
 *                                                     BoundsCalculator::combine);
 * }</pre>
 * <p>
 * The BoundsCalculator can be used as a 
 * {@linkplain java.util.stream.Stream#collect(Collector) reduction}
   * <pre> {@code
 * BoundsCalculator stats = figures.stream()
 *     .collect(BoundsCalculator.collectBounds(Figure::getBoundsInWorld));
 *}</pre>
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BoundsCalculator {
    private double minX = Double.POSITIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;
    private double maxX = Double.NEGATIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;

    /** Constructs a new instance with initially empty bounds. */
    public BoundsCalculator() {
    }

    /**
     * Adds a another value to the bounds. 
     *
     * @param value the input value
     */
    public void accept(@Nonnull Bounds value) {
        minX = Math.min(minX, value.getMinX());
        maxX = Math.max(maxX, value.getMaxX());
        minY = Math.min(minY, value.getMinY());
        maxY = Math.max(maxY, value.getMaxY());
    }
    
    /**
     * Combines the state of another {@code BoundsCalculator} into this
     * one.
     *
     * @param other another {@code BoundsCalculator}
     * @throws NullPointerException if {@code other} is null
     */
    public void combine(@Nonnull BoundsCalculator other) {
        minX = Math.min(minX, other.minX);
        maxX = Math.max(maxX, other.maxX);
        minY = Math.min(minY, other.minY);
        maxY = Math.max(maxY, other.maxY);
    }
    
    /**
     * Getter.
     * @return the calculated bounds 
     */
    @Nonnull
    public Bounds getBounds() {
        return new BoundingBox(minX,minY,maxX-minX,maxY-minY);
    }
    

}
