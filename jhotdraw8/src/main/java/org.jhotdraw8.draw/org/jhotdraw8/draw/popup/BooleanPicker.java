package org.jhotdraw8.draw.popup;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.util.Resources;

public class BooleanPicker {
    private ContextMenu contextMenu;
    MenuItem initialItem;
    MenuItem noneItem;
    MenuItem trueItem;
    MenuItem falseItemm;
    private final SetProperty<Figure> figures = new SimpleSetProperty<>();
    private final ObjectProperty<WriteableStyleableMapAccessor<Boolean>> mapAccessor = new SimpleObjectProperty<>();
    private final ObjectProperty<DrawingView> drawingView = new SimpleObjectProperty<>();

    public BooleanPicker() {
        init();
    }


    public ObservableSet<Figure> getFigures() {
        return figures.get();
    }

    public SetProperty<Figure> figuresProperty() {
        return figures;
    }

    public void setFigures(ObservableSet<Figure> figures) {
        this.figures.set(figures);
    }

    public WriteableStyleableMapAccessor<Boolean> getMapAccessor() {
        return mapAccessor.get();
    }

    public ObjectProperty<WriteableStyleableMapAccessor<Boolean>> mapAccessorProperty() {
        return mapAccessor;
    }

    public void setMapAccessor(WriteableStyleableMapAccessor<Boolean> mapAccessor) {
        this.mapAccessor.set(mapAccessor);
    }

    public DrawingView getDrawingView() {
        return drawingView.get();
    }

    public ObjectProperty<DrawingView> drawingViewProperty() {
        return drawingView;
    }

    public void setDrawingView(DrawingView drawingView) {
        this.drawingView.set(drawingView);
    }

    private void init() {
        Resources labels = DrawLabels.getResources();
        contextMenu = new ContextMenu();
        initialItem = new MenuItem();
        noneItem = new MenuItem();
        trueItem = new MenuItem();
        falseItemm = new MenuItem();
        initialItem.setOnAction(this::setToInitialize);
        noneItem.setOnAction(this::setToNone);
        trueItem.setOnAction(this::setToTrue);
        falseItemm.setOnAction(this::setToFalse);
        labels.configureMenuItem(initialItem, "value.initial");
        labels.configureMenuItem(noneItem, "value.none");
        labels.configureMenuItem(trueItem, "value.true");
        labels.configureMenuItem(falseItemm, "value.false");
        contextMenu.getItems().addAll(initialItem, noneItem, trueItem, falseItemm);
    }

    private void update() {
        Converter<Boolean> converter = getMapAccessor().getConverter();
        if (converter instanceof CssConverter<?>) {
            CssConverter<?> cssConverter = (CssConverter<?>) converter;
            noneItem.setVisible(cssConverter.isNullable());
        }
    }

    private void setToNone(ActionEvent actionEvent) {
        DrawingModel m = getModel();
        if (m != null) {
            for (Figure f : getFigures()) {
                m.set(f, getMapAccessor(), null);
            }
        }
    }

    private DrawingModel getModel() {
        DrawingView v = getDrawingView();
        return v == null ? null : v.getModel();
    }

    private void setToTrue(ActionEvent actionEvent) {
        DrawingModel m = getModel();
        if (m != null) {
            for (Figure f : getFigures()) {
                m.set(f, getMapAccessor(), true);
            }
        }
    }

    private void setToFalse(ActionEvent actionEvent) {
        DrawingModel m = getModel();
        if (m != null) {
            for (Figure f : getFigures()) {
                m.set(f, getMapAccessor(), false);
            }
        }
    }

    private void setToInitialize(ActionEvent actionEvent) {
        DrawingModel m = getModel();
        if (m != null) {
            for (Figure f : getFigures()) {
                m.remove(f, getMapAccessor());
            }
        }

    }


    public void show(Node anchor, double screenX, double screenY) {
        update();
        contextMenu.show(anchor, screenX, screenY);
    }
}
