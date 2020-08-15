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
import javafx.collections.ObservableSet;
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
import javafx.scene.layout.BorderPane;
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
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.beans.NonNullObjectProperty;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.draw.constrain.Constrainer;
import org.jhotdraw8.draw.constrain.NullConstrainer;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.gui.ZoomableScrollPane;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.model.SimpleDrawingModel;
import org.jhotdraw8.draw.render.InteractiveDrawingRenderer;
import org.jhotdraw8.draw.render.InteractiveHandleRenderer;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.tree.TreeBreadthFirstSpliterator;
import org.jhotdraw8.tree.TreeModelEvent;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

public class SimpleDrawingViewNew extends AbstractDrawingView {
    @NonNull
    private final ZoomableScrollPane zoomableScrollPane = ZoomableScrollPane.create();
    @NonNull
    final
    private SimpleDrawingViewNode node = new SimpleDrawingViewNode();
    private boolean constrainerNodeValid;

    private class SimpleDrawingViewNode extends BorderPane implements EditableComponent {

        public SimpleDrawingViewNode() {
            setFocusTraversable(true);
            setId("drawingView");
        }

        @Override
        public void selectAll() {
            SimpleDrawingViewNew.this.selectAll();
        }

        @Override
        public void clearSelection() {
            SimpleDrawingViewNew.this.clearSelection();
        }

        @Override
        public ReadOnlyBooleanProperty selectionEmptyProperty() {
            return SimpleDrawingViewNew.this.selectedFiguresProperty().emptyProperty();
        }

        @Override
        public void deleteSelection() {
            SimpleDrawingViewNew.this.deleteSelection();
        }

        @Override
        public void duplicateSelection() {
            SimpleDrawingViewNew.this.duplicateSelection();
        }

        @Override
        public void cut() {
            SimpleDrawingViewNew.this.cut();
        }

        @Override
        public void copy() {
            SimpleDrawingViewNew.this.copy();
        }

        @Override
        public void paste() {
            SimpleDrawingViewNew.this.paste();
        }
    }

    @NonNull
    private final NonNullObjectProperty<DrawingModel> model //
            = new NonNullObjectProperty<>(this, MODEL_PROPERTY, new SimpleDrawingModel());
    @NonNull
    private final ReadOnlyObjectWrapper<Drawing> drawing = new ReadOnlyObjectWrapper<>(this, DRAWING_PROPERTY);
    @NonNull
    private final ObjectProperty<Figure> activeParent = new SimpleObjectProperty<>(this, ACTIVE_PARENT_PROPERTY);
    @NonNull
    private final NonNullObjectProperty<Constrainer> constrainer = new NonNullObjectProperty<>(this, CONSTRAINER_PROPERTY, new NullConstrainer());
    @NonNull
    private final ReadOnlyBooleanWrapper focused = new ReadOnlyBooleanWrapper(this, FOCUSED_PROPERTY);
    @NonNull
    private final Region background = new Region();
    @NonNull
    private final StackPane foreground = new StackPane();
    private final InteractiveDrawingRenderer drawingRenderer = new InteractiveDrawingRenderer();
    private final InteractiveHandleRenderer handleRenderer = new InteractiveHandleRenderer();
    private boolean isLayoutValid = true;
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
    public @NonNull NonNullObjectProperty<Constrainer> constrainerProperty() {
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
        return drawingRenderer.findFigure(vx, vy, figures);
    }

    @Override
    public @Nullable Figure findFigure(double vx, double vy) {
        return drawingRenderer.findFigure(vx, vy);
    }

    @Override
    public @Nullable Node findFigureNode(@NonNull Figure figure, double vx, double vy) {
        return drawingRenderer.findFigureNode(figure, vx, vy);
    }

    @Override
    public @NonNull List<Figure> findFigures(double vx, double vy, boolean decompose) {
        return drawingRenderer.findFigures(vx, vy, decompose);
    }

    @Override
    public @NonNull List<Figure> findFiguresInside(double vx, double vy, double vwidth, double vheight, boolean decompose) {
        return drawingRenderer.findFiguresInside(vx, vy, vwidth, vheight, decompose);
    }

    @Override
    public @NonNull List<Figure> findFiguresIntersecting(double vx, double vy, double vwidth, double vheight, boolean decompose) {
        return drawingRenderer.findFiguresIntersecting(vx, vy, vwidth, vheight, decompose);
    }

    @Override
    public @Nullable Handle findHandle(double vx, double vy) {
        return handleRenderer.findHandle(vx, vy);
    }

    @Override
    public @NonNull ReadOnlyBooleanProperty focusedProperty() {
        return focused.getReadOnlyProperty();
    }

    @Override
    public @NonNull Set<Figure> getFiguresWithCompatibleHandle(Collection<Figure> figures, Handle handle) {
        return handleRenderer.getFiguresWithCompatibleHandle(figures, handle);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public @Nullable Node getNode(Figure f) {
        return drawingRenderer.getNode(f);
    }

    @Override
    public @NonNull Transform getViewToWorld() {
        return zoomableScrollPane.getViewToContent();
    }

    @Override
    public Bounds getVisibleRect() {
        return zoomableScrollPane.getViewRect();
    }

    @Override
    public @NonNull Transform getWorldToView() {
        return zoomableScrollPane.getContentToView();
    }

    private void initBindings() {
        CustomBinding.bind(drawing, model, DrawingModel::drawingProperty);
        model.addListener(this::onDrawingModelChanged);
        model.get().setRoot(new SimpleDrawing());
        onDrawingModelChanged(model, null, model.getValue());
        drawingRenderer.modelProperty().bind(this.modelProperty());
        drawingRenderer.clipBoundsProperty().bind(zoomableScrollPane.viewRectProperty());
        drawingRenderer.editorProperty().bind(this.editorProperty());
        drawingRenderer.setDrawingView(this);
        handleRenderer.modelProperty().bind(this.modelProperty());
        handleRenderer.setSelectedFigures(getSelectedFigures());
        handleRenderer.editorProperty().bind(this.editorProperty());
        handleRenderer.setDrawingView(this);
        zoomFactorProperty().addListener(this::onZoomFactorChanged);
        constrainer.addListener((o, oldValue, newValue) -> updateConstrainer(oldValue, newValue));
        zoomableScrollPane.viewRectProperty().addListener(o -> revalidateConstrainer());
    }

    private void updateConstrainer(@Nullable Constrainer oldValue, @Nullable Constrainer newValue) {
        if (oldValue != null) {
            foreground.getChildren().remove(oldValue.getNode());
            oldValue.removeListener(this::onConstrainerInvalidated);
        }
        if (newValue != null) {
            foreground.getChildren().add(0, newValue.getNode());
            newValue.getNode().applyCss();
            newValue.updateNode(this);
            newValue.addListener(this::onConstrainerInvalidated);
            invalidateConstrainer();
            repaint();
        }
    }

    private void invalidateConstrainer() {
        constrainerNodeValid = false;
    }

    private void onConstrainerInvalidated(Observable o) {
        invalidateConstrainer();
        repaint();
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
        node.setCenter(zoomableScrollPane.getNode());


        background.setBackground(new Background(new BackgroundFill(
                new ImagePattern(createCheckerboardImage(Color.WHITE, Color.LIGHTGRAY, 8), 0, 0, 16, 16, false)
                , CornerRadii.EMPTY, Insets.EMPTY)));
        background.setManaged(false);
        zoomableScrollPane.getContentChildren().add(drawingRenderer.getNode());
        zoomableScrollPane.getBackgroundChildren().add(background);
        zoomableScrollPane.getForegroundChildren().addAll(
                handleRenderer.getNode(),
                foreground);
        foreground.setManaged(false);

        background.setBorder(
                new Border(
                        new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(24)),
                        new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))
                ));
        CustomBinding.bind(drawing, model, DrawingModel::drawingProperty);
    }

    @Override
    protected void invalidateHandles() {

    }

    @Override
    public void jiggleHandles() {
        handleRenderer.jiggleHandles();
    }

    @Override
    public @NonNull NonNullObjectProperty<DrawingModel> modelProperty() {
        return model;
    }

    private void onDrawingChanged() {
    }

    private void onDrawingModelChanged(Observable o, @Nullable DrawingModel oldValue, @Nullable DrawingModel newValue) {
        if (oldValue != null) {
            oldValue.removeTreeModelListener(treeModelListener);
        }
        if (newValue != null) {
            newValue.addTreeModelListener(treeModelListener);
            revalidateLayout();
        }
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
        case NODE_REMOVED_FROM_PARENT:
        case NODE_ADDED_TO_TREE:
            break;
        case NODE_REMOVED_FROM_TREE:
            onNodeRemoved(f);
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

    private void onNodeRemoved(Figure f) {
        ObservableSet<Figure> selectedFigures = getSelectedFigures();
        for (Figure d : f.preorderIterable()) {
            selectedFigures.remove(d);
        }
        repaint();
    }

    @Override
    public void recreateHandles() {
        handleRenderer.recreateHandles();
    }

    @Nullable
    private Runnable repainter = null;

    @Override
    protected void repaint() {
        if (repainter == null) {
            repainter = () -> {
                repainter = null;
                validateConstrainer();
            };
            Platform.runLater(repainter);
        }
    }

    private void revalidateConstrainer() {
        invalidateConstrainer();
        repaint();
    }

    private void validateConstrainer() {
        if (!constrainerNodeValid) {
            updateConstrainerNode();
            constrainerNodeValid = true;
        }
    }

    private void updateConstrainerNode() {
        Constrainer c = getConstrainer();
        if (c != null) {
            c.updateNode(this);
        }
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

    /**
     * Selects all enabled and selectable figures in all enabled layers.
     */

    public void selectAll() {
        ArrayList<Figure> figures = new ArrayList<>();
        Drawing d = getDrawing();
        if (d != null) {
            for (Figure layer : d.getChildren()) {
                if (layer.isEditable() && layer.isVisible()) {
                    for (Figure f : layer.getChildren()) {
                        if (f.isSelectable()) {
                            figures.add(f);
                        }
                    }
                }
            }
        }
        getSelectedFigures().clear();
        getSelectedFigures().addAll(figures);
    }

    public void clearSelection() {
        getSelectedFigures().clear();
    }

    public void deleteSelection() {
        ArrayList<Figure> figures = new ArrayList<>(getSelectedFigures());
        DrawingModel model = getModel();

        // Also delete dependent figures.
        Deque<Figure> cascade = new ArrayDeque<>(figures);
        for (Figure f : figures) {
            for (Figure ff : f.preorderIterable()) {
                StreamSupport.stream(new TreeBreadthFirstSpliterator<Figure>(
                                figure -> () ->
                                        figure.getLayoutObservers().stream()
                                                .filter(x -> x.getLayoutSubjects().size() == 1).iterator()
                                , ff),
                        false)
                        .forEach(cascade::addFirst);
            }
        }
        for (Figure f : cascade) {
            if (f.isDeletable()) {
                for (Figure d : f.preorderIterable()) {
                    model.disconnect(d);
                }
                model.removeFromParent(f);
            }
        }
    }

    public void duplicateSelection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
