/* @(#)BoundsLocator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.locator;

/**
 * A locator that specifies a point that is relative to the path of a
 * {@link org.jhotdraw8.draw.figure.PathIterableFigure}.
 * <p>
 * The locator has the following parameters:
 * <dl>
 * <dt>{@code relativePath}</dt><dd>Defines a position on the path
 * of the figure, relative to to the length of the path.
 * Where {@code 0.0} lies at the start of the path
 * and {@code 1.0} at the end.</dd>
 * <dt>{@code offset}</dt><dd>Defines an absolute distance perpendicular
 * to the path at the {@code relativePath} position.
 * Where {@code 0.0} lies on the path, and a positive value lies
 * to the right side of the path, and a negative to the left side of the path.</dd>
 * </dl>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PathLocator {
}
