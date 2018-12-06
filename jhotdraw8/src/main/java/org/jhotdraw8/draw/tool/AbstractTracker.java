/* @(#)AbstractAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javax.annotation.Nonnull;
import org.jhotdraw8.app.AbstractDisableable;

/**
 * AbstractAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractTracker extends AbstractDisableable implements Tracker {

    // ---
    // Fields
    // ---
    protected final BorderPane node = new BorderPane();

    // ---
    // Constructors
    // ---
    /**
     * Creates a new instance.
     */
    public AbstractTracker() {

    }

    // ---
    // Properties
    // ---
    // ---
    // Behaviors
    // ---
    @Nonnull
    @Override
    public Node getNode() {
        return node;
    }
}
