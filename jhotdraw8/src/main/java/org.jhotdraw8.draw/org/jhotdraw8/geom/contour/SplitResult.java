/*
 * @(#)SplitResult.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.contour;

/**
 * Result of splitting a segment v1 to v2.
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
public class SplitResult {
    /// Updated starting vertex.
    PlineVertex updatedStart;
    /// Vertex at the split point.
    PlineVertex splitVertex;
};