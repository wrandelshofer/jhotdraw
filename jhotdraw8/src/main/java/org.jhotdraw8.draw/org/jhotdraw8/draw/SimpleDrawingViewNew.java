/*
 * @(#)SimpleDrawingViewNew.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.NonNullProperty;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.draw.constrain.Constrainer;
import org.jhotdraw8.draw.constrain.NullConstrainer;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.gui.ZoomableScrollPane;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.model.DrawingModelEvent;
import org.jhotdraw8.draw.model.SimpleDrawingModel;
import org.jhotdraw8.draw.render.InteractiveDrawingRenderer;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.tree.TreeModelEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SimpleDrawingViewNew extends AbstractDrawingView {
    @NonNull
    private final ZoomableScrollPane zoomableScrollPane = ZoomableScrollPane.create();
    @NonNull
    private final NonNullProperty<DrawingModel> model //
            = new NonNullProperty<>(this, MODEL_PROPERTY, new SimpleDrawingModel());
    @NonNull
    private final ReadOnlyObjectWrapper<Drawing> drawing = new ReadOnlyObjectWrapper<>(this, DRAWING_PROPERTY);
    @NonNull
    private final ObjectProperty<Figure> activeParent = new SimpleObjectProperty<>(this, ACTIVE_PARENT_PROPERTY);
    @NonNull
    private final NonNullProperty<Constrainer> constrainer = new NonNullProperty<>(this, CONSTRAINER_PROPERTY, new NullConstrainer());
    @NonNull
    private final ReadOnlyBooleanWrapper focused = new ReadOnlyBooleanWrapper(this, FOCUSED_PROPERTY);
    @NonNull
    private final Region background = new Region();
    @NonNull
    private final StackPane foreground = new StackPane();
    private final InteractiveDrawingRenderer renderer = new InteractiveDrawingRenderer();
    private boolean isLayoutValid = true;
    @NonNull
    private final Listener<DrawingModelEvent> drawingModelListener = this::onDrawingModelEvent;
    @NonNull
    private final Listener<TreeModelEvent<Figure>> treeModelListener = this::onTreeModelEvent;


    public SimpleDrawingViewNew() {
        initView();
        initBindings();
        init();
    }

    protected void init() {

    }

    @Override
    public @NonNull ObjectProperty<Figure> activeParentProperty() {
        return activeParent;
    }

    @Override
    public @NonNull NonNullProperty<Constrainer> constrainerProperty() {
        return constrainer;
    }

    @NonNull
    private Image createCheckerboardImage(Color c1, Color c2, int size) {
        WritableImage img = new WritableImage(size * 2, size * 2);
        PixelWriter w = img.getPixelWriter();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                w.setColor(x, y, c1);
                w.setColor(x + size, y + size, c1);
                w.setColor(x + size, y, c2);
                w.setColor(x, y + size, c2);
            }
        }
        return img;
    }

    @Override
    public @NonNull ReadOnlyObjectProperty<Drawing> drawingProperty() {
        return drawing.getReadOnlyProperty();
    }

    @Override
    public @Nullable Figure findFigure(double vx, double vy, Set<Figure> figures) {
        return null;
    }

    @Override
    public @Nullable Figure findFigure(double vx, double vy) {
        return null;
    }

    @Override
    public @Nullable Node findFigureNode(@NonNull Figure figure, double vx, double vy) {
        return null;
    }

    @Override
    public @NonNull List<Figure> findFigures(double vx, double vy, boolean decompose) {
        return null;
    }

    @Override
    public @NonNull List<Figure> findFiguresInside(double vx, double vy, double vwidth, double vheight, boolean decompose) {
        return new ArrayList<>();
    }

    @Override
    public @NonNull List<Figure> findFiguresIntersecting(double vx, double vy, double vwidth, double vheight, boolean decompose) {
        return null;
    }

    @Override
    public @Nullable Handle findHandle(double vx, double vy) {
        return null;
    }

    @Override
    public @NonNull ReadOnlyBooleanProperty focusedProperty() {
        return focused.getReadOnlyProperty();
    }

    @Override
    public @NonNull Set<Figure> getFiguresWithCompatibleHandle(Collection<Figure> figures, Handle handle) {
        return null;
    }

    @Override
    public Node getNode() {
        return zoomableScrollPane.getNode();
    }

    @Override
    public @Nullable Node getNode(Figure f) {
        return null;
    }

    @Override
    public @NonNull Transform getViewToWorld() {
        return zoomableScrollPane.getViewToContent();
    }

    @Override
    public Bounds getVisibleRect() {
        return null;
    }

    @Override
    public @NonNull Transform getWorldToView() {
        return null;
    }

    private void initBindings() {
        CustomBinding.bind(drawing, model, DrawingModel::drawingProperty);
        model.addListener(this::onDrawingModelChanged);
        model.get().setRoot(new SimpleDrawing());
        onDrawingModelChanged(model, null, model.getValue());
        renderer.modelProperty().bind(this.modelProperty());
        zoomFactorProperty().addListener(this::onZoomFactorChanged);
        renderer.clipBoundsProperty().bind(zoomableScrollPane.viewRectProperty());
    }

    private void onZoomFactorChanged(Observable observable) {
        revalidateLayout();
    }

    private void initView() {
        String emptyCss = "/org/jhotdraw8/draw/empty.css";
        URL emptyCssUrl = SimpleDrawingViewNew.class.getResource(emptyCss);
        if (emptyCssUrl == null) {
            throw new RuntimeException("could not load " + emptyCss);
        }
        zoomableScrollPane.setSubSceneUserAgentStylesheet(
                emptyCssUrl.toString()
        );


        background.setBackground(new Background(new BackgroundFill(
                new ImagePattern(createCheckerboardImage(Color.WHITE, Color.LIGHTGRAY, 8), 0, 0, 16, 16, false)
                , CornerRadii.EMPTY, Insets.EMPTY)));
        background.setManaged(false);
        zoomableScrollPane.getContentChildren().add(renderer.getNode());
        zoomableScrollPane.getBackgroundChildren().add(background);
        zoomableScrollPane.getForegroundChildren().add(foreground);
        foreground.setManaged(false);
        background.setBorder(
                new Border(
                        new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(24))
                        ,
                        new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
                ));
        CustomBinding.bind(drawing, model, DrawingModel::drawingProperty);
    }

    @Override
    protected void invalidateHandles() {

    }

    @Override
    public void jiggleHandles() {

    }

    @Override
    public @NonNull NonNullProperty<DrawingModel> modelProperty() {
        return model;
    }

    private void onDrawingChanged() {
    }

    private void onDrawingModelChanged(Observable o, @Nullable DrawingModel oldValue, @Nullable DrawingModel newValue) {
        if (oldValue != null) {
            oldValue.removeTreeModelListener(treeModelListener);
            oldValue.removeDrawingModelListener(drawingModelListener);
        }
        if (newValue != null) {
            newValue.addDrawingModelListener(drawingModelListener);
            newValue.addTreeModelListener(treeModelListener);
            revalidateLayout();
        }
    }

    private void onDrawingModelEvent(DrawingModelEvent event) {

        Figure f = event.getNode();
        switch (event.getEventType()) {
        case LAYOUT_CHANGED:
            if (f == getDrawing()) {
                revalidateLayout();
                repaint();
            }
            break;
        case STYLE_CHANGED:
            repaint();
            break;
        case PROPERTY_VALUE_CHANGED:
            if (f == getDrawing()) {
                revalidateLayout();
                repaint();
            }
            break;
        case TRANSFORM_CHANGED:
            break;
        default:
            throw new UnsupportedOperationException(event.getEventType()
                    + " not supported");
        }

    }

    private void onFigureAdded(Figure f) {
    }

    private void onFigureRemoved(Figure f) {
    }

    private void onFigureRemovedFromDrawing(Figure f) {
    }

    private void onNodeChanged(Figure f) {
        if (f == getDrawing()) {
            revalidateLayout();
        }
    }

    private void onRootChanged() {
        onDrawingChanged();
        revalidateLayout();
        repaint();

    }

    private void onSubtreeNodesChanged(Figure f) {
    }

    @Override
    protected void onToolChanged(Observable observable, Tool oldValue, Tool newValue) {
        if (oldValue != null) {
            foreground.getChildren().remove(oldValue.getNode());
            oldValue.setDrawingView(null);
            focused.unbind();
        }
        if (newValue != null) {
            foreground.getChildren().add(newValue.getNode());
            newValue.setDrawingView(this);
            focused.bind(newValue.getNode().focusedProperty());
        }
    }

    private void onTreeModelEvent(TreeModelEvent<Figure> event) {
        Figure f = event.getNode();
        switch (event.getEventType()) {
        case NODE_ADDED_TO_PARENT:
            onFigureAdded(f);
            break;
        case NODE_REMOVED_FROM_PARENT:
            onFigureRemoved(f);
            break;
        case NODE_ADDED_TO_TREE:
            onFigureRemovedFromDrawing(f);
            break;
        case NODE_REMOVED_FROM_TREE:
            for (Figure d : f.preorderIterable()) {
                getSelectedFigures().remove(d);
            }
            repaint();
            break;
        case NODE_CHANGED:
            onNodeChanged(f);
            break;
        case ROOT_CHANGED:
            onRootChanged();
            break;
        case SUBTREE_NODES_CHANGED:
            onSubtreeNodesChanged(f);
            break;
        default:
            throw new UnsupportedOperationException(event.getEventType()
                    + " not supported");
        }
    }

    @Override
    public void recreateHandles() {

    }

    @Override
    protected void repaint() {

    }

    private void revalidateLayout() {
        if (isLayoutValid) {
            isLayoutValid = false;
            Platform.runLater(this::validateLayout);
        }
    }

    @Override
    public void scrollRectToVisible(Bounds boundsInView) {

    }

    private void updateLayout() {
        Drawing drawing = getDrawing();
        Bounds bounds = drawing == null ? new BoundingBox(0, 0, 10, 10) : drawing.getLayoutBounds();
        double f = getZoomFactor();
        double invf = 1 / f;
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        zoomableScrollPane.setContentSize(w, h);
        double p = 24;
        background.resizeRelocate(-p, -p, w * f + 2 * p, h * f + 2 * p);
        foreground.resize(w * f, h * f);
    }

    private void validateLayout() {
        if (!isLayoutValid) {
            isLayoutValid = true;
            updateLayout();
        }
    }

    @Override
    public @NonNull DoubleProperty zoomFactorProperty() {
        return zoomableScrollPane.zoomFactorProperty();
    }
}
