/*
 * @(#)StyleAttributesInspector.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.css.FigureSelectorModel;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.tree.TreeModelEvent;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 */
public class StyleAttributesInspector extends AbstractStyleAttributesInspector<Figure>
        implements Inspector<DrawingView> {

    protected final @NonNull ObjectProperty<DrawingView> subject = new SimpleObjectProperty<>(this, SUBJECT_PROPERTY);

    public @NonNull ObjectProperty<DrawingView> subjectProperty() {
        return subject;
    }

    private final InvalidationListener modelInvalidationHandler = new InvalidationListener() {

        @Override
        public void invalidated(Observable observable) {
            invalidateTextArea(observable);
        }

    };

    private final Listener<TreeModelEvent<Figure>> treeModelListener = event -> {
        invalidateTextArea(event.getSource());
    };

    private final @NonNull ChangeListener<DrawingModel> modelChangeHandler = (ObservableValue<? extends DrawingModel> observable, DrawingModel oldValue, DrawingModel newValue) -> {
        if (oldValue != null) {
            oldValue.removeListener(modelInvalidationHandler);
            oldValue.removeTreeModelListener(treeModelListener);
        }
        if (newValue != null) {
            newValue.addListener(modelInvalidationHandler);
            newValue.addTreeModelListener(treeModelListener);
        }
    };

    {
        subject.addListener(this::onDrawingViewChanged);
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
    protected @Nullable Object get(@NonNull Figure f, @NonNull WritableStyleableMapAccessor<Object> finalSelectedAccessor) {
        return getDrawingModel().get(f, finalSelectedAccessor);
    }

    @Override
    protected @Nullable WritableStyleableMapAccessor<?> getAccessor(SelectorModel<Figure> selectorModel, @NonNull Figure f, String propertyNamespace, String propertyName) {
        if (selectorModel instanceof FigureSelectorModel) {
            FigureSelectorModel m = (FigureSelectorModel) selectorModel;
            return m.getAccessor(f, propertyNamespace, propertyName);
        }
        return null;
    }

    @Override
    protected @Nullable Converter<?> getConverter(SelectorModel<Figure> selectorModel, @NonNull Figure f, String namespace, String name) {
        if (selectorModel instanceof FigureSelectorModel) {
            FigureSelectorModel m = (FigureSelectorModel) selectorModel;
            return m.getConverter(f, namespace, name);
        }
        return null;
    }

    protected @Nullable Drawing getDrawing() {
        DrawingView view = getSubject();
        return view == null ? null : view.getDrawing();
    }

    protected @Nullable DrawingModel getDrawingModel() {
        DrawingView view = getSubject();
        return view == null ? null : view.getModel();
    }

    @Override
    protected @NonNull Iterable<Figure> getEntities() {
        return getDrawing().breadthFirstIterable();
    }

    @Override
    protected @Nullable Figure getRoot() {
        DrawingView subject = getSubject();
        return subject == null ? null : subject.getDrawing();
    }

    @Override
    protected @Nullable StylesheetsManager<Figure> getStyleManager() {
        Drawing d = getDrawing();
        return d == null ? null : d.getStyleManager();
    }

    /**
     * Can be overridden by subclasses. This implementation is empty.
     *
     * @param observable
     * @param oldValue   the old drawing view
     * @param newValue   the new drawing view
     */
    protected void onDrawingViewChanged(ObservableValue<? extends DrawingView> observable, @Nullable DrawingView oldValue, @Nullable DrawingView newValue) {
        if (oldValue != null) {
            oldValue.modelProperty().removeListener(modelChangeHandler);
            modelChangeHandler.changed(oldValue.modelProperty(), oldValue.getModel(), null);
            selectionProperty().unbind();
        }
        if (newValue != null) {
            newValue.modelProperty().addListener(modelChangeHandler);
            modelChangeHandler.changed(newValue.modelProperty(), null, newValue.getModel());
            invalidateTextArea(observable);
            selectionProperty().bind(newValue.selectedFiguresProperty());
        }
    }

    @Override
    protected void remove(@NonNull Figure f, WritableStyleableMapAccessor<Object> finalSelectedAccessor) {
        getDrawingModel().remove(f, finalSelectedAccessor);
    }

    @Override
    protected void set(@NonNull Figure f, WritableStyleableMapAccessor<Object> finalSelectedAccessor, Object o) {
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
}
