/* @(#)Inspector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.scene.Node;
import org.jhotdraw8.draw.DrawingView;

/**
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Inspector {

    public void setDrawingView(DrawingView view);

    public Node getNode();
}
