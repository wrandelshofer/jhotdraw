/* @(#)Inspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.inspector;

import javafx.scene.Node;
import org.jhotdraw8.draw.DrawingView;

/**
 *
 * @author werni
 */
public interface Inspector {

    public void setDrawingView(DrawingView view);

    public Node getNode();
}
