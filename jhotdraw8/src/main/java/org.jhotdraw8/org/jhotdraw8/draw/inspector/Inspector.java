/* @(#)Inspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.scene.Node;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.DrawingView;

/**
 * API for inspectors.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Inspector {

    void setDrawingView(@Nullable DrawingView view);

    Node getNode();
}
