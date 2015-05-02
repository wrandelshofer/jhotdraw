/* @(#)AbstractAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import org.jhotdraw.app.action.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.jhotdraw.app.AbstractDisableable;
import org.jhotdraw.beans.OptionalProperty;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.event.Listener;
import org.jhotdraw.util.Resources;

/**
 * AbstractAction.
 * @author Werner Randelshofer
 */
public abstract class AbstractTracker extends AbstractDisableable implements Tracker {
    // ---
    // Fields
    // ---
    protected final BorderPane node = new BorderPane();

    // ---
    // Constructors
    // ---
    /** Creates a new instance.
     */
    public AbstractTracker() {

    }

    // ---
    // Properties
    // ---

    // ---
    // Behaviors
    // ---

    @Override
    public Node getNode() {
        return node;
    }
}
