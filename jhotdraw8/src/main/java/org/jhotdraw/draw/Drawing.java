/* @(#)Drawing.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw.collection.Key;

/**
 * A {@code Drawing} is a container for {@link Figure}s.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Drawing extends Figure {

    public final static Key<Rectangle2D> BOUNDS = new Key<>("bounds", Rectangle2D.class, new Rectangle2D(0, 0, 640, 480));
    public final static Key<Paint> BACKGROUND_PAINT = new Key<>("background", Paint.class, Color.WHITE);

}
