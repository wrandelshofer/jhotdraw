/*
 * @(#)PlineIntersectsResult.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.contour;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a collection of intersects found.
 * <p>
 * This code has been derived from Cavalier Contours [1].
 * <p>
 * References:
 * <dl>
 *     <dt>[1] Cavalier Contours</dt>
 *     <dd>Cavalier Contours, Copyright (c) 2019 Jedidiah Buck McCready, MIT License.
 *     <a href="https://github.com/jbuckmccready/CavalierContours">github.com</a></dd>
 * </dl>
 */
public class PlineIntersectsResult {
    List<PlineIntersect> intersects = new ArrayList<>();
    List<PlineCoincidentIntersect> coincidentIntersects = new ArrayList<>();

    @Override
    public String toString() {
        return "PlineIntersectsResult{" +
                "intersects=" + intersects +
                ", coincidentIntersects=" + coincidentIntersects +
                '}';
    }
}
