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
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.beans.NonNullObjectProperty;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.css.MacOSSystemColorConverter;
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
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

public class SimpleDrawingView extends AbstractDrawingView {
    @NonNull
    private final ZoomableScrollPane zoomableScrollPane = ZoomableScrollPane.create();
    @NonNull
    final
    private SimpleDrawingViewNode node = new SimpleDrawingViewNode();
    private boolean constrainerNodeValid;
    private boolean backgroundNodeValid;

    private class SimpleDrawingViewNode extends BorderPane implements EditableComponent {

        public SimpleDrawingViewNode() {
            setFocusTraversable(true);
            setId("drawingView");
        }

        @Override
        public void selectAll() {
            SimpleDrawingView.this.selectAll();
        }

        @Override
        public void clearSelection() {
            SimpleDrawingView.this.clearSelection();
        }

        @Override
        public ReadOnlyBooleanProperty selectionEmptyProperty() {
            return SimpleDrawingView.this.selectedFiguresProperty().emptyProperty();
        }

        @Override
        public void deleteSelection() {
            SimpleDrawingView.this.deleteSelection();
        }

        @Override
        public void duplicateSelection() {
            SimpleDrawingView.this.duplicateSelection();
        }

        @Override
        public void cut() {
            SimpleDrawingView.this.cut();
        }

        @Override
        public void copy() {
            SimpleDrawingView.this.copy();
        }

        @Override
        public void paste() {
            SimpleDrawingView.this.paste();
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


    public SimpleDrawingView() {
        initView();
        initBindings();
        init();
    }

    protected void init() {
        drawingRenderer.setRenderContext(this);
        set(SYSTEM_COLOR_CONVERTER_KEY, new MacOSSystemColorConverter());

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
        return worldToView(zoomableScrollPane.getVisibleContentRect());
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
        drawingRenderer.clipBoundsProperty().bind(zoomableScrollPane.visibleContentRectProperty());
        drawingRenderer.editorProperty().bind(this.editorProperty());
        drawingRenderer.setDrawingView(this);
        handleRenderer.modelProperty().bind(this.modelProperty());
        handleRenderer.setSelectedFigures(getSelectedFigures());
        handleRenderer.editorProperty().bind(this.editorProperty());
        handleRenderer.setDrawingView(this);
        zoomFactorProperty().addListener(this::onZoomFactorChanged);
        constrainer.addListener(this::onConstrainerChanged);
        zoomableScrollPane.visibleContentRectProperty().addListener(this::onViewRectChanged);
        zoomableScrollPane.contentToViewProperty().addListener(this::onContentToViewChanged);
        CustomBinding.bind(drawing, model, DrawingModel::drawingProperty);
        CustomBinding.bind(focused, toolProperty(), Tool::focusedProperty);
    }

    private void onContentToViewChanged(Observable observable) {
        updateBackgroundNode();
    }

    private void onConstrainerChanged(@NonNull Observable o, @Nullable Constrainer oldValue, @Nullable Constrainer newValue) {
        if (oldValue != null) {
            foreground.getChildren().remove(oldValue.getNode());
            oldValue.removeListener(this::onConstrainerInvalidated);
        }
        if (newValue != null) {
            Node node = newValue.getNode();
            node.setManaged(false);
            foreground.getChildren().add(0, node);
            node.applyCss();
            newValue.updateNode(this);
            newValue.addListener(this::onConstrainerInvalidated);
            invalidateConstrainer();
            repaint();
        }
    }

    private void invalidateConstrainer() {
        constrainerNodeValid = false;
    }

    private void invalidateBackground() {
        backgroundNodeValid = false;
    }

    private void onConstrainerInvalidated(Observable o) {
        invalidateConstrainer();
        repaint();
    }

    private void onZoomFactorChanged(Observable observable) {
        revalidateLayout();
    }

    private void onViewRectChanged(Observable observable, Bounds oldValue, Bounds newValue) {
        revalidateLayout();
    }

    private void initView() {
        String emptyCss = "/org/jhotdraw8/draw/empty.css";
        URL emptyCssUrl = SimpleDrawingView.class.getResource(emptyCss);
        if (emptyCssUrl == null) {
            throw new RuntimeException("could not load " + emptyCss);
        }
        zoomableScrollPane.setSubSceneUserAgentStylesheet(
                emptyCssUrl.toString()
        );
        node.setCenter(zoomableScrollPane.getNode());

        background.getStyleClass().add("canvasPane");
        background.setBackground(new Background(new BackgroundFill(
                new ImagePattern(
                        createCheckerboardImage(Color.WHITE, Color.LIGHTGRAY, 8),
                        0, 0, 16, 16, false)
                , CornerRadii.EMPTY, Insets.EMPTY)));
        background.setManaged(false);
        BorderStrokeStyle outsideStroke = new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 24.0, 0, Collections.emptyList());
        Border backgroundBorder = new Border(
                new BorderStroke(Color.TRANSPARENT, outsideStroke, CornerRadii.EMPTY, new BorderWidths(24))
        );
        background.setBorder(backgroundBorder);
        zoomableScrollPane.getContentChildren().add(drawingRenderer.getNode());
        zoomableScrollPane.getBackgroundChildren().add(background);
        zoomableScrollPane.getForegroundChildren().addAll(
                handleRenderer.getNode(),
                foreground);
        foreground.setManaged(false);

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
        }
        if (newValue != null) {
            Node node = newValue.getNode();
            node.setManaged(true);// we want the tool to fill the view
            foreground.getChildren().add(node);
            newValue.setDrawingView(this);
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

    private void revalidateBackground() {
        invalidateBackground();
        repaint();
    }

    private void validateBackground() {
        if (!backgroundNodeValid) {
            updateBackgroundNode();
            backgroundNodeValid = true;
        }
    }

    private void updateBackgroundNode() {
        Drawing drawing = getDrawing();
        Bounds bounds = drawing == null ? new BoundingBox(0, 0, 10, 10) : drawing.getLayoutBounds();
        Bounds bounds1 = worldToView(bounds);
        double x = bounds1.getMinX();
        double y = bounds1.getMinY();
        double w = bounds1.getWidth();
        double h = bounds1.getHeight();

        double p = 0;
        background.resizeRelocate(x - p, y - p, w + 2 * p, h + 2 * p);
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
            validateLayout();
            //Platform.runLater(this::validateLayout);
        }
    }

    @Override
    public void scrollRectToVisible(Bounds boundsInView) {
        zoomableScrollPane.scrollViewRectToVisible(boundsInView);
    }

    private void updateLayout() {
        Drawing drawing = getDrawing();
        Bounds bounds = drawing == null ? new BoundingBox(0, 0, 10, 10) : drawing.getLayoutBounds();
        double f = getZoomFactor();
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        zoomableScrollPane.setContentSize(w, h);
        Bounds vp = zoomableScrollPane.getViewportRect();
        foreground.resize(vp.getWidth(), vp.getHeight());

        handleRenderer.invalidateHandleNodes();
        handleRenderer.repaint();
        updateConstrainerNode();
        updateBackgroundNode();
    }

    private void validateLayout() {
        if (!isLayoutValid) {
            updateLayout();
            isLayoutValid = true;
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
                StreamSupport.stream(new TreeBreadthFirstSpliterator<>(
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
