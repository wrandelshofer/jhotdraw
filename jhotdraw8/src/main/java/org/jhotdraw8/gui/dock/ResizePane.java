/* @(#)ResizePane.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import javax.annotation.Nonnull;

/**
 * ResizePane.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ResizePane extends BorderPane {

    private final BooleanProperty userResizable = new SimpleBooleanProperty(true);

    public ResizePane() {
        ResizeButton rb = new ResizeButton();
        rb.setTarget(this);
        setBottom(rb);
        rb.visibleProperty().bind(userResizable);
    }

    public boolean isUserResizable() {
        return userResizable.get();
    }

    public void setUserResizable(boolean value) {
        userResizable.set(value);
  }

    @Nonnull
    public BooleanProperty userResizableProperty() {
        return userResizable;
    }

    public void setContent(Node n) {
        setCenter(n);
    }

    public Node getContent() {
        return getCenter();
    }

}
