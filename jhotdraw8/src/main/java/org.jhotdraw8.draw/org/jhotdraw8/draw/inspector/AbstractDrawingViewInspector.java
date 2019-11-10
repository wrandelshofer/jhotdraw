/*
 * @(#)AbstractDrawingViewInspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.model.DrawingModel;

/**
 * AbstractDrawingInspector.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractDrawingViewInspector implements Inspector<DrawingView> {

    final protected ObjectProperty<DrawingView> subject = new SimpleObjectProperty<>();

    {
        subject.addListener(this::handleDrawingViewChanged);
    }

    @NonNull
    public ObjectProperty<DrawingView> subjectProperty() {
        return subject;
    }

    protected DrawingModel getDrawingModel() {
        return getSubject().getModel();
    }

    /**
     * Can be overridden by subclasses. This implementation is empty.
     *
     * @param observable
     * @param oldValue   the old drawing view
     * @param newValue   the new drawing view
     */
    protected void handleDrawingViewChanged(ObservableValue<? extends DrawingView> observable, @Nullable DrawingView oldValue, @Nullable DrawingView newValue) {

    }
}
