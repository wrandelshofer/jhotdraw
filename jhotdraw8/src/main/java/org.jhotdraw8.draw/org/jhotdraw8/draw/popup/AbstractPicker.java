package org.jhotdraw8.draw.popup;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

public abstract class AbstractPicker<T> implements Picker<T> {
    private final SetProperty<Figure> figures = new SimpleSetProperty<>();
    private final ObjectProperty<WriteableStyleableMapAccessor<T>> mapAccessor = new SimpleObjectProperty<>();
    private final ObjectProperty<DrawingView> drawingView = new SimpleObjectProperty<>();

    public SetProperty<Figure> figuresProperty() {
        return figures;
    }

    public ObjectProperty<WriteableStyleableMapAccessor<T>> mapAccessorProperty() {
        return mapAccessor;
    }

    public ObjectProperty<DrawingView> drawingViewProperty() {
        return drawingView;
    }

    protected void applyInitialValue() {
        DrawingModel m = getModel();
        if (m != null) {
            for (Figure f : getFigures()) {
                m.remove(f, getMapAccessor());
            }
        }
    }

    protected void applyValue(T b) {
        DrawingModel m = getModel();
        if (m != null) {
            for (Figure f : getFigures()) {
                m.set(f, getMapAccessor(), b);
            }
        }
    }

    protected DrawingModel getModel() {
        DrawingView v = getDrawingView();
        return v == null ? null : v.getModel();
    }

    protected T getCurrentValue() {
        for (Figure f : getFigures()) {
            return f.get(getMapAccessor());
        }
        return null;
    }

}
