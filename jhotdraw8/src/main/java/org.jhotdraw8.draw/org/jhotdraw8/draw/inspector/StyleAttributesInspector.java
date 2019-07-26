/*
 * @(#)StyleAttributesInspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.css.FigureSelectorModel;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StyleAttributesInspector extends AbstractStyleAttributesInspector<Figure>
        implements Inspector<DrawingView> {

    @Override
    protected Iterable<Figure> getEntities() {
        return getDrawing().breadthFirstIterable();
    }

    @Override
    protected void fireInvalidated(Figure f) {
        DrawingModel m = getDrawingModel();
        m.fireStyleInvalidated(f);
        m.fireNodeInvalidated(f);
        m.fireTransformInvalidated(f);
        m.fireLayoutInvalidated(f);

    }

    @Override
    protected Object get(Figure f, WriteableStyleableMapAccessor<Object> finalSelectedAccessor) {
        return getDrawingModel().get(f, finalSelectedAccessor);
    }

    @Override
    protected WriteableStyleableMapAccessor<?> getAccessor(SelectorModel<Figure> selectorModel, Figure f, String propertyNamespace, String propertyName) {
        if (selectorModel instanceof FigureSelectorModel) {
            FigureSelectorModel m = (FigureSelectorModel) selectorModel;
            return m.getAccessor(f, propertyNamespace, propertyName);
        }
        return null;
    }

    @Override
    protected Converter<?> getConverter(SelectorModel<Figure> selectorModel, Figure f, String namespace, String name) {
        if (selectorModel instanceof FigureSelectorModel) {
            FigureSelectorModel m = (FigureSelectorModel) selectorModel;
            return m.getConverter(f, namespace, name);
        }
        return null;
    }

    @Override
    protected StylesheetsManager<Figure> getStyleManager() {
        Drawing d = getDrawing();
        return d == null ? null : d.getStyleManager();
    }



    @Override
    protected void remove(Figure f, WriteableStyleableMapAccessor<Object> finalSelectedAccessor) {
        getDrawingModel().remove(f, finalSelectedAccessor);
    }

    @Override
    protected void set(Figure f, WriteableStyleableMapAccessor<Object> finalSelectedAccessor, Object o) {
        getDrawingModel().set(f, finalSelectedAccessor, o);
    }

    @Override
    protected void setHelpText(String helpText) {
        DrawingView view = getSubject();
        DrawingEditor editor = view == null ? null : view.getEditor();
        if (editor != null) {
            editor.setHelpText(helpText);
        }
    }

    @Override
    protected void showSelection() {
        DrawingView drawingView = getSubject();
        if (drawingView != null) {
            drawingView.scrollSelectedFiguresToVisible();
            drawingView.jiggleHandles();
        }
    }

    @Override
    protected Figure getRoot() {
        DrawingView subject = getSubject();
        return subject == null ? null : subject.getDrawing();
    }

    final protected ObjectProperty<DrawingView> subject = new SimpleObjectProperty<>();

    {
        subject.addListener(this::handleDrawingViewChanged);
    }

    public ObjectProperty<DrawingView> subjectProperty() {
        return subject;
    }

    protected Drawing getDrawing() {
        DrawingView view = getSubject();
        return view == null ? null : view.getDrawing();
    }

    protected DrawingModel getDrawingModel() {
        DrawingView view = getSubject();
        return view == null ? null : view.getModel();
    }

    private final InvalidationListener modelInvalidationHandler = new InvalidationListener() {

        @Override
        public void invalidated(Observable observable) {
            textAreaInvalidated(observable);
        }

    };
    @Nonnull
    private final ChangeListener<DrawingModel> modelChangeHandler = (ObservableValue<? extends DrawingModel> observable, DrawingModel oldValue, DrawingModel newValue) -> {
        if (oldValue != null) {
            oldValue.removeListener(modelInvalidationHandler);
        }
        if (newValue != null) {
            newValue.addListener(modelInvalidationHandler);
        }
    };

    /**
     * Can be overridden by subclasses. This implementation is empty.
     *
     * @param observable
     * @param oldValue   the old drawing view
     * @param newValue   the new drawing view
     */
    protected void handleDrawingViewChanged(ObservableValue<? extends DrawingView> observable, @Nullable DrawingView oldValue, @Nullable DrawingView newValue) {
        if (oldValue != null) {
            oldValue.modelProperty().removeListener(modelChangeHandler);
            modelChangeHandler.changed(oldValue.modelProperty(), oldValue.getModel(), null);
            selectionProperty().unbind();
        }
        if (newValue != null) {
            newValue.modelProperty().addListener(modelChangeHandler);
            modelChangeHandler.changed(newValue.modelProperty(), null, newValue.getModel());
            textAreaInvalidated(observable);
            selectionProperty().bind(newValue.selectedFiguresProperty());
        }
    }

}
