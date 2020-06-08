/*
 * @(#)ResizePane.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
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

    private final ResizeButton rb = new ResizeButton();

    public ResizePane() {
        rb.setTarget(this);
        setBottom(rb);
        rb.visibleProperty().bind(userResizable);
    }

    public void setResizeAxis(DockAxis axis) {
        switch (axis) {
        case X:
            rb.setCursor(Cursor.H_RESIZE);
            setBottom(null);
            setRight(rb);
            break;
        case Y:
        case Z:
        default:
            rb.setCursor(Cursor.V_RESIZE);
            setRight(null);
            setBottom(rb);
            break;
        }
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
