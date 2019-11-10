/*
 * @(#)ResizePane.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.jhotdraw8.annotation.NonNull;

/**
 * ResizePane.
 *
 * @author Werner Randelshofer
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

    @NonNull
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
