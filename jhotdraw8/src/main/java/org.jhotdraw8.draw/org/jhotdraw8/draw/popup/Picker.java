package org.jhotdraw8.draw.popup;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

public interface Picker<T> {
    void show(Node anchor, double screenX, double screenY);

    SetProperty<Figure> figuresProperty();

    default ObservableSet<Figure> getFigures() {
        return figuresProperty().get();
    }

    default void setFigures(ObservableSet<Figure> figures) {
        figuresProperty().set(figures);
    }

    default WriteableStyleableMapAccessor<T> getMapAccessor() {
        return mapAccessorProperty().get();
    }

    ObjectProperty<WriteableStyleableMapAccessor<T>> mapAccessorProperty();

    default void setMapAccessor(WriteableStyleableMapAccessor<T> mapAccessor) {
        mapAccessorProperty().set(mapAccessor);
    }

    default DrawingView getDrawingView() {
        return drawingViewProperty().get();
    }

    ObjectProperty<DrawingView> drawingViewProperty();


    default void setDrawingView(DrawingView drawingView) {
        drawingViewProperty().set(drawingView);
    }

}
