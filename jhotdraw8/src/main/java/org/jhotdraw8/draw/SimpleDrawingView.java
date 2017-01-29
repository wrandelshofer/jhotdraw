/* @(#)SimpleDrawingView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw;

import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.model.DrawingModelEvent;
import org.jhotdraw8.draw.model.DrawingModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.beans.NonnullProperty;
import org.jhotdraw8.draw.constrain.Constrainer;
import org.jhotdraw8.draw.constrain.NullConstrainer;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.draw.handle.Handle;
import static java.lang.Math.*;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.draw.model.SimpleDrawingModel;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;
import org.jhotdraw8.util.ReversedList;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class SimpleDrawingView extends AbstractDrawingView implements EditableComponent {

    private boolean constrainerNodeValid;
    private boolean recreateHandles;

    @Override
    public ReadOnlyBooleanProperty selectionEmptyProperty() {
        return selectedFiguresProperty().emptyProperty();
    }

    private static class FixedSizedGroup extends Group {

    }

    private Group drawingSubScene;
    private Group overlaysSubScene;

    private BorderPane toolPane;
    private Rectangle canvasPane;

    private Group handlesPane;
    private Group gridPane;

    private Group drawingPane;
    private Pane overlaysPane;

    private final static double INVSQRT2 = 1.0 / Math.sqrt(2);

    /**
     * The number of nodes that are maximally updated per frame.
     */
    private int maxUpdate = 100;

    /**
     * This is the JavaFX Node which is used to represent this drawing view. in
     * a JavaFX scene graph.
     */
    private Pane rootPane;

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

    private SimpleDrawingViewNode node;

    private final Listener<DrawingModelEvent> modelHandler = new Listener<DrawingModelEvent>() {

        @Override
        public void handle(DrawingModelEvent event) {
            Figure f = event.getFigure();
            switch (event.getEventType()) {
                case FIGURE_ADDED_TO_PARENT:
                    handleFigureAdded(f);
                    break;
                case FIGURE_REMOVED_FROM_PARENT:
                    handleFigureRemoved(f);
                    break;
                case SUBTREE_ADDED_TO_DRAWING:
                    handleFigureRemovedFromDrawing(f);
                    break;
                case SUBTREE_REMOVED_FROM_DRAWING:
                    for (Figure d : f.preorderIterable()) {
                        getSelectedFigures().remove(d);
                    }
                    repaint();
                    break;
                case NODE_CHANGED:
                    handleNodeChanged(f);
                    break;
                case LAYOUT_CHANGED:
                    if (f == getDrawing()) {
                        invalidateConstrainerNode();
                        invalidateWorldViewTransforms();
                        repaint();
                    }
                    break;
                case STYLE_CHANGED:
                    repaint();
                    break;
                case ROOT_CHANGED:
                    handleDrawingChanged();
                    updateLayout();
                    repaint();
                    break;
                case SUBTREE_NODES_CHANGED:
                    handleSubtreeNodesChanged(f);
                    repaint();
                    break;
                case PROPERTY_VALUE_CHANGED:
                case LAYOUT_SUBJECT_CHANGED:
                case TRANSFORM_CHANGED:
                    break;
                default:
                    throw new UnsupportedOperationException(event.getEventType()
                            + " not supported");
            }
        }

    };
    /**
     * The drawingProperty holds the drawing that is presented by this drawing
     * view.
     */
    private final NonnullProperty<DrawingModel> drawingModel //
            = new NonnullProperty<DrawingModel>(this, MODEL_PROPERTY, new SimpleDrawingModel()) {
        private DrawingModel oldValue = null;

        @Override
        protected void fireValueChangedEvent() {
            DrawingModel newValue = get();
            super.fireValueChangedEvent();
            handleNewDrawingModel(oldValue, newValue);
            oldValue = newValue;
        }
    };

    /**
     * The constrainer property holds the constrainer for this drawing view
     */
    private final NonnullProperty<Constrainer> constrainer = new NonnullProperty<>(this, CONSTRAINER_PROPERTY, new NullConstrainer());

    {
        constrainer.addListener((o, oldValue, newValue) -> updateConstrainer(oldValue, newValue));
    }
    private boolean handlesAreValid;

    private Transform viewToWorldTransform = null;
    private Transform worldToViewTransform = null;
    /**
     * If too many figures are selected, we only draw one outline handle for all
     * selected figures instead of one outline handle for each selected figure.
     */
    private int tooManySelectedFigures = 20;

    /**
     * Selection tolerance. Selectable margin around a figure.
     */
    public final static double TOLERANCE = 5;
    public final static double TOLERANCE_SQUARED = TOLERANCE*TOLERANCE;

    private final ObjectProperty<Layer> activeLayer = new SimpleObjectProperty<>(this, ACTIVE_LAYER_PROPERTY);
    private final ReadOnlyObjectWrapper<Drawing> drawing = new ReadOnlyObjectWrapper<>(this, DRAWING_PROPERTY);

    /**
     * This is just a wrapper around the focusedProperty of the JavaFX Node
     * which is used to render this view.
     */
    private final ReadOnlyBooleanWrapper focused = new ReadOnlyBooleanWrapper(this, FOCUSED_PROPERTY);
    /**
     * The zoom factor.
     */
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, ZOOM_FACTOR_PROPERTY, 1.0) {

        @Override
        protected void fireValueChangedEvent() {
            double newValue = get();
            Scale st = new Scale(newValue, newValue);
            if (drawingPane != null) {
                if (drawingPane.getTransforms().isEmpty()) {
                    drawingPane.getTransforms().add(st);
                } else {
                    drawingPane.getTransforms().set(0, st);
                }
            }
            updateLayout();
            invalidateHandleNodes();
            if (constrainer.get() != null) {
                constrainer.get().updateNode(SimpleDrawingView.this);
            }

            // zoom towards the center of the drawing
            /*ScrollPane scrollPane = getScrollPane();
                if (scrollPane != null) {
                    scrollPane.setVvalue(0.5);
                    scrollPane.setHvalue(0.5);
                }*/
        }
    };

    /**
     * XXX use this to center scroll pane on view when zooming.
     */
    private ScrollPane getScrollPane() {
        Parent p = (Parent) getNode();
        while (p != null && !(p instanceof ScrollPane)) {
            p = p.getParent();
        }
        return (ScrollPane) p;
    }

    /**
     * Maps each JavaFX node to a handle in the drawing view.
     */
    private final Map<Node, Handle> nodeToHandleMap = new LinkedHashMap<>();
    /**
     * Maps each JavaFX node to a figure in the drawing.
     */
    private final Map<Node, Figure> nodeToFigureMap = new IdentityHashMap<>();
    /**
     * Maps JavaFX nodes to a figure. Note that the DrawingView may contain
     * JavaFX nodes which have no mapping. this is usually the case, when a
     * Figure is represented by multiple nodes. Then only the parent of these
     * nodes is associated with the figure.
     */
    private final Map<Figure, Node> figureToNodeMap = new IdentityHashMap<>();
    /**
     * This is the set of figures which are out of sync with their JavaFX node.
     * We do not wrap the IdentityHashMap into a Set to avoid an additional
     * level of indirection.
     */
    private final Set<Figure> dirtyFigureNodes = new HashSet<>();
    /**
     * This is the set of handles which are out of sync with their JavaFX node.
     * We do not wrap the IdentityHashMap into a Set to avoid an additional
     * level of indirection.
     */
    private final Set<Figure> dirtyHandles = new HashSet<>();
    /**
     * The set of all handles which were produced by selected figures.
     */
    private final Map<Figure, List<Handle>> handles = new IdentityHashMap<>();
    /**
     * The set of all secondary handles. One handle at a time may create
     * secondary handles.
     */
    private final LinkedList<Handle> secondaryHandles = new LinkedList<>();

    private Runnable repainter = null;

    private InvalidationListener modelInvalidationListener = o -> repaint();

    public SimpleDrawingView() {
        init();
    }

    private void init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            rootPane = loader.load(SimpleDrawingView.class.getResourceAsStream("SimpleDrawingView.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        canvasPane = new Rectangle();
        canvasPane.setId("canvasPane");
        canvasPane.setFill(new ImagePattern(createCheckerboardImage(Color.WHITE, Color.LIGHTGRAY, 8), 0, 0, 16, 16, false));

        drawingSubScene = new Group();
        drawingSubScene.setManaged(false);
        drawingSubScene.setMouseTransparent(true);
        overlaysSubScene = new Group();
        overlaysSubScene.setManaged(false);
        rootPane.getChildren().addAll(drawingSubScene, overlaysSubScene);

        drawingPane = new Group();
        //drawingPane.setCacheHint(CacheHint.QUALITY);
        drawingPane.setCache(true);
        //drawingPane.setScaleX(zoomFactor.get());
        //drawingPane.setScaleY(zoomFactor.get());
        drawingSubScene.getChildren().addAll(canvasPane, drawingPane);

        toolPane = new BorderPane();
        toolPane.setId("toolPane");
        toolPane.setBackground(Background.EMPTY);
        toolPane.setManaged(false);
        handlesPane = new Group();
        handlesPane.setManaged(false);
        handlesPane.setMouseTransparent(true);
        gridPane = new Group();
        gridPane.setManaged(false);
        gridPane.setMouseTransparent(true);
        overlaysPane = new Pane();
        overlaysPane.setBackground(Background.EMPTY);
        overlaysPane.getChildren().addAll(gridPane, handlesPane, toolPane);
        overlaysPane.setManaged(false);
        overlaysSubScene.getChildren().add(overlaysPane);

        // We use a change listener instead of an invalidation listener here,
        // because we only want to update the layout, when the new value is
        // different from the old value!
        drawingPane.layoutBoundsProperty().addListener((observer, oldValue, newValue) -> {
            updateLayout();
        });

        drawingModel.get().setRoot(new SimpleDrawing());
        handleNewDrawingModel(null, drawingModel.get());

        // Set stylesheet
        rootPane.getStylesheets().add("org/jhotdraw8/draw/SimpleDrawingView.css");

        // set root
        node = new SimpleDrawingViewNode();
        node.setCenter(rootPane);
    }

    private void invalidateFigureNode(Figure f) {
        dirtyFigureNodes.add(f);
        if (handles.containsKey(f)) {
            dirtyHandles.add(f);
        }
    }

    @Override
    public Transform getWorldToView() {
        if (worldToViewTransform == null) {
            // We try to avoid the Scale transform as it is slower than a Translate transform
            Transform tr = new Translate(drawingPane.getTranslateX() - overlaysPane.getTranslateX(), drawingPane.getTranslateY() - overlaysPane.getTranslateX());
            double zoom = zoomFactor.get();
            worldToViewTransform = (zoom == 1.0) ? tr : Transforms.concat(tr,new Scale(zoom, zoom));
        }
        return worldToViewTransform;
    }

    @Override
    public Transform getViewToWorld() {
        if (viewToWorldTransform == null) {
            // We try to avoid the Scale transform as it is slower than a Translate transform
            Transform tr = new Translate(-drawingPane.getTranslateX() + overlaysPane.getTranslateX(), -drawingPane.getTranslateY() + overlaysPane.getTranslateX());
            double zoom = zoomFactor.get();
            viewToWorldTransform = (zoom == 1.0) ? tr : Transforms.concat(new Scale(1.0 / zoom, 1.0 / zoom),tr);
        }
        return viewToWorldTransform;
    }
    private Bounds previousScaledBounds = null;

    /**
     * Updates the layout of the drawing pane and the panes laid over it.
     */
    private void updateLayout() {
        if (node == null) {
            return;
        }

        double f = getZoomFactor();
        Drawing d = getDrawing();
        double dw = d.get(Drawing.WIDTH);
        double dh = d.get(Drawing.HEIGHT);
        int imgw = Math.min(16000, Math.max(1, (int) (dw)));
        int imgh = Math.min(16000, Math.max(1, (int) (dh)));

        if (drawingImageView != null) {
            if (drawingImage == null || drawingImage.getWidth() != imgw || drawingImage.getHeight() != imgh) {
                drawingImage = null;
                drawingImageView.setImage(null);
                drawingImage = new WritableImage(imgw, imgh);
                drawingImageView.setImage(drawingImage);
                //dirtyFigureNodes.add(d);
                // repaint();
            }
        }

        Bounds bounds = drawingPane.getLayoutBounds();
        double x = bounds.getMinX() * f;
        double y = bounds.getMinY() * f;
        double w = bounds.getWidth() * f;
        double h = bounds.getHeight() * f;

        Bounds scaledBounds = new BoundingBox(x, y, w, h);
        if (Objects.equals(scaledBounds, previousScaledBounds)) {
            return;
        }
        previousScaledBounds = scaledBounds;
//    drawingPane.setTranslateX(max(0, -x));
        // drawingPane.setTranslateY(max(0, -y));
        if (d != null) {
            canvasPane.setTranslateX(max(0, -x));
            canvasPane.setTranslateY(max(0, -y));
            canvasPane.setWidth(dw * f);
            canvasPane.setHeight(dh * f);
        }
        //backgroundPane.layout();

        double padding = 20;
        double lw = max(max(0, x) + w, max(0, -x) + dw * f);
        double lh = max(max(0, y) + h, max(0, -y) + dh * f);
        //overlaysPane.setTranslateX(-padding);
        //overlaysPane.setTranslateY(-padding);
        drawingPane.setTranslateX(padding);
        drawingPane.setTranslateY(padding);
        canvasPane.setTranslateX(padding);
        canvasPane.setTranslateY(padding);
        toolPane.resize(lw + padding * 2, lh + padding * 2);
        toolPane.layout();

        overlaysPane.setClip(new Rectangle(0, 0, lw + padding * 2, lh + padding * 2));

        // drawingPane.setClip(new Rectangle(0,0,lw,lh));
        rootPane.setPrefSize(lw + padding * 2, lh + padding * 2);
        rootPane.setMaxSize(lw + padding * 2, lh + padding * 2);

        invalidateWorldViewTransforms();
        invalidateHandleNodes();
    }

    @Override
    public ReadOnlyObjectProperty<Drawing> drawingProperty() {
        return drawing.getReadOnlyProperty();
    }

    @Override
    public Node getNode() {
        return node;
    }

    private void removeNode(Figure f) {
        Node oldNode = figureToNodeMap.remove(f);
        if (oldNode != null) {
            nodeToFigureMap.remove(oldNode);
        }
        dirtyFigureNodes.remove(f);
    }

    private void clearNodes() {
        figureToNodeMap.clear();
        nodeToFigureMap.clear();
        dirtyFigureNodes.clear();
    }

    @Override
    public Node getNode(Figure f) {
        Node n = figureToNodeMap.get(f);
        if (n == null) {
            n = f.createNode(this);
            figureToNodeMap.put(f, n);
            nodeToFigureMap.put(n, f);
            dirtyFigureNodes.add(f);
        }
        return n;
    }

    @Override
    public NonnullProperty<DrawingModel> modelProperty() {
        return drawingModel;
    }

    @Override
    public NonnullProperty<Constrainer> constrainerProperty() {
        return constrainer;
    }

    /**
     * Set drawingImageView to a non-null value to force rendering into a image.
     * This will render the drawing without adding it to the scene graph.
     */
    private ImageView drawingImageView = null;//new ImageView();
    private WritableImage drawingImage = null;
    boolean renderIntoImage = false;

    private void handleDrawingChanged() {
        clearNodes();
        clearSelection();
        drawingPane.getChildren().clear();
        activeLayer.set(null);
        Drawing d = getModel().getRoot();
        drawing.set(d);
        if (d != null) {
            if (drawingImageView != null) {
                drawingPane.getChildren().add(drawingImageView);
            } else {
                drawingPane.getChildren().add(getNode(d));
            }
            dirtyFigureNodes.add(d);
            updateLayout();
            handleSubtreeNodesChanged(d);
            repaint();

            for (int i = d.getChildren().size() - 1; i >= 0; i--) {
                Layer layer = (Layer) d.getChild(i);
                if (!layer.isEditable() && layer.isVisible()) {
                    activeLayer.set(layer);
                    break;
                }

            }
        }
    }

    private void handleSubtreeNodesChanged(Figure figures) {
        for (Figure f : figures.preorderIterable()) {
            dirtyFigureNodes.add(f);
            dirtyHandles.add(f);
        }
    }

    private void updateConstrainer(Constrainer oldValue, Constrainer newValue) {
        if (oldValue != null) {
            gridPane.getChildren().remove(oldValue.getNode());
            oldValue.removeListener(this::handleConstrainerInvalidated);
        }
        if (newValue != null) {
            gridPane.getChildren().add(newValue.getNode());
            newValue.getNode().applyCss();
            newValue.updateNode(this);
            newValue.addListener(this::handleConstrainerInvalidated);
            invalidateConstrainerNode();
            repaint();
        }
    }

    private void handleConstrainerInvalidated(Observable o) {
        invalidateConstrainerNode();
        repaint();
    }

    private void updateTreeStructure(Figure parent) {
        // Since we don't know which figures have been removed from
        // the drawing, we have to get rid of them on ourselves.
        // XXX This is a really slow operation. If each figure would store a
        // reference to its drawing it would perform better.
        Drawing drawing = getDrawing();
        for (Figure f : new ArrayList<Figure>(figureToNodeMap.keySet())) {
            if (f.getRoot() != drawing) {
                removeNode(f);
            }
        }
        handleSubtreeNodesChanged(parent);
    }

    private void handleNewDrawingModel(DrawingModel oldValue, DrawingModel newValue) {
        if (oldValue != null) {
            clearSelection();
            oldValue.removeDrawingModelListener(modelHandler);
            oldValue.removeListener(modelInvalidationListener);
            drawing.setValue(null);
        }
        if (newValue != null) {
            newValue.addDrawingModelListener(modelHandler);
            newValue.addListener(modelInvalidationListener);
            handleDrawingChanged();
            updateLayout();
            repaint();
        }
    }

    private void handleFigureAdded(Figure figure) {
        for (Figure f : figure.preorderIterable()) {
            invalidateFigureNode(f);
        }
        repaint();
    }

    private void handleFigureRemoved(Figure figure) {
        for (Figure f : figure.preorderIterable()) {
            removeNode(f);
        }
        invalidateHandles();
        repaint();
    }

    private void handleFigureRemovedFromDrawing(Figure figure) {
        final ObservableSet<Figure> selectedFigures = getSelectedFigures();
        for (Figure f : figure.preorderIterable()) {
            selectedFigures.remove(f);
        }
    }

    private void handleNodeChanged(Figure f) {
        invalidateFigureNode(f);
        if (f == getDrawing()) {
            updateLayout();
            if (constrainer.get() != null) {
                handleConstrainerInvalidated(constrainer.get());
            }
        }
        repaint();
    }

    public void dump(Node n, int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print(".");
        }
        System.out.print(n + " lb: " + Geom.toString(n.getLayoutBounds()));
        Figure f = nodeToFigureMap.get(n);
        if (f != null) {
            System.out.println(" flb: " + Geom.toString(f.getBoundsInParent()));
        } else {
            System.out.println();
        }
        if (n instanceof Parent) {
            Parent p = (Parent) n;
            for (Node c : p.getChildrenUnmodifiable()) {
                dump(c, depth + 1);
            }
        }
    }

    private void updateNodes() {
        if (!renderIntoImage) {
            // create copies of the lists to allow for concurrent modification
            Figure[] copyOfDirtyFigureNodes = dirtyFigureNodes.toArray(new Figure[dirtyFigureNodes.size()]);
            dirtyFigureNodes.clear();
            for (Figure f : copyOfDirtyFigureNodes) {
                f.updateNode(this, getNode(f));
            }
            if (copyOfDirtyFigureNodes.length != 0 && drawingImageView != null) {
                renderIntoImage = true;
                long start = System.currentTimeMillis();
                Node n = getNode(getDrawing());
                SnapshotParameters params = new SnapshotParameters();
                n.snapshot(result -> {
                    System.out.println("rendering done in:" + (System.currentTimeMillis() - start) + "ms");
                    renderIntoImage = false;
                    drawingImage = result.getImage();
                    drawingImageView.setImage(result.getImage());
                    return null;
                }, params, drawingImage);
            }
        }

        if (!recreateHandles) {
            Figure[] copyOfDirtyHandles = dirtyHandles.toArray(new Figure[dirtyHandles.size()]);
            dirtyHandles.clear();
            for (Figure f : copyOfDirtyHandles) {
                List<Handle> hh = handles.get(f);
                if (hh != null) {
                    for (Handle h : hh) {
                        h.updateNode(this);
                    }
                }
            }
        }
        for (Handle h : secondaryHandles) {
            h.updateNode(this);
        }

        if (!constrainerNodeValid) {
            constrainerNodeValid = true;
            Constrainer c = getConstrainer();
            if (c != null) {
                c.updateNode(this);
            }
        }
    }

    /**
     * Repaints the view.
     */
    public void repaint() {
        if (repainter == null) {
            repainter = () -> {
                getModel().validate();
                updateNodes();
                validateHandles();
                //dump(getNode(getDrawing()),0);
                repainter = null;
            };
            Platform.runLater(repainter);
        }
    }

    @Override
    public ReadOnlyBooleanProperty focusedProperty() {
        return focused.getReadOnlyProperty();
    }

    @Override
    protected void updateTool(Tool oldValue, Tool newValue) {
        if (oldValue != null) {
            Tool t = oldValue;
            toolPane.setCenter(null);
            t.setDrawingView(null);
            focused.unbind();
        }
        if (newValue != null) {
            Tool t = newValue;
            toolPane.setCenter(t.getNode());
            t.setDrawingView(this);
            focused.bind(t.getNode().focusedProperty());
        }
    }

    @Override
    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    @Override
    public Handle findHandle(double vx, double vy) {
        if (recreateHandles) {
            return null;
        }
        for (Map.Entry<Node, Handle> e : new ReversedList<>(nodeToHandleMap.entrySet())) {
            final Node node = e.getKey();
            final Handle handle = e.getValue();
            if (!handle.isSelectable())continue;
            if (handle.contains(this,vx,vy, TOLERANCE_SQUARED)) {
                   return handle;
            } else {
                if (contains(node, new Point2D(vx, vy), TOLERANCE)) {
                    return handle;
                }
            }
        }
        /*
        for (Node n : handlesPane.getChildren()) {
            Point2D pl = n.parentToLocal(vx, vy);
            if (isInsideRadius(n, pl, HANDLE_TOLERANCE)) {
                Handle h = nodeToHandleMap.get(n);
                if (h.isSelectable()) {
                    return h;
                }
            }
        }*/
        return null;
    }

    @Override
    public Figure findFigure(double vx, double vy) {
        Drawing dr = getDrawing();
        Figure f = findFigureRecursive((Parent) getNode(dr), viewToDrawing(vx, vy), 0.0);

        if (f == null) {
            f = findFigureRecursive((Parent) getNode(dr), viewToDrawing(vx, vy), TOLERANCE / getZoomFactor());
        }
        return f;
    }

    private Figure findFigureRecursive(Parent p, Point2D pp, double tolerance) {
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            if (!n.isVisible()) {
                continue;
            }
            Point2D pl = n.parentToLocal(pp);
            if (contains(n, pl, tolerance)) {
                Figure f = nodeToFigureMap.get(n);
                if (f == null || !f.isSelectable()) {
                    if (n instanceof Parent) {
                        f = findFigureRecursive((Parent) n, pl, tolerance);
                    }
                }
                if (f != null && f.isSelectable()) {
                    return f;
                }
            }
        }
        return null;
    }

    @Override
    public Figure findFigure(double vx, double vy, Set<Figure> figures) {
        Drawing dr = getDrawing();
        Figure f = findFigureRecursiveInSet((Parent) getNode(dr), viewToWorld(vx, vy), figures, TOLERANCE / getZoomFactor());

        return f;
    }

    private Figure findFigureRecursiveInSet(Parent p, Point2D pp, Set<Figure> figures, double tolerance) {
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            if (!n.isVisible()) {
                continue;
            }
            Point2D pl = n.parentToLocal(pp);
            double localTolerance = n.parentToLocal(0, 0).distance(n.parentToLocal(tolerance * INVSQRT2, tolerance * INVSQRT2));
            if (contains(n, pl, localTolerance)) {
                Figure f = nodeToFigureMap.get(n);
                if (f == null || !f.isSelectable() || !figures.contains(f)) {
                    if (n instanceof Parent) {
                        f = findFigureRecursiveInSet((Parent) n, pl, figures, localTolerance);
                    }
                }
                if (f != null && f.isSelectable() && figures.contains(f)) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * Returns true if the node contains the specified point within a a
     * tolerance.
     *
     * @param node The node
     * @param point The point in local coordinates
     * @param tolerance The maximal distance the point is allowed to be away
     * from the
     * @return true if the node contains the point
     */
    private boolean contains(Node node, Point2D point, double tolerance) {
        if (tolerance == 0) {
            return node.contains(point);
        }
        if (node instanceof Shape) {
            Shape shape = (Shape) node;
            if (shape.getFill() == null) {
                return Shapes.outlineContains(Shapes.awtShapeFromFX(shape), new java.awt.geom.Point2D.Double(point.getX(), point.getY()), tolerance);
            } else {
                return shape.contains(point);
            }

        } else if (node instanceof Rectangle) {
            return Geom.contains(node.getBoundsInLocal(), point, tolerance);
        } else if (node instanceof Shape) {// no special treatment for other shapes
            return node.contains(point);
        } else if (node instanceof Group) {
            if (Geom.contains(node.getBoundsInLocal(), point, tolerance)) {
                for (Node child : ((Group) node).getChildren()) {
                    if (contains(child, child.parentToLocal(point), tolerance)) {
                        return true;
                    }
                }
            }
            return false;
        } else { // foolishly assumes that all other nodes are rectangular and opaque
            return Geom.contains(node.getBoundsInLocal(), point, tolerance);
        }
    }

    /**
     * Returns true if the point is inside the radius from the center of the
     * node.
     *
     * @param node The node
     * @param point The point in local coordinates
     * @param squaredRadius The square of the radius in which the node must be
     * @return true if the node contains the point
     */
    private boolean isInsideRadius(Node node, Point2D point, double squaredRadius) {
        Bounds b = node.getBoundsInLocal();
        double cx = b.getMinX() + b.getWidth() * 0.5;
        double cy = b.getMinY() + b.getHeight() * 0.5;
        double dx = point.getX() - cx;
        double dy = point.getY() - cy;
        return dx * dx + dy * dy < squaredRadius;
    }

    @Override
    public List<Figure> findFigures(double vx, double vy, boolean decompose) {
        Transform vt = getViewToWorld();
        Point2D pp = vt.transform(vx, vy);
        List<Figure> list = new LinkedList<Figure>();
        findFiguresRecursive((Parent) figureToNodeMap.get(getDrawing()), pp, list, decompose);
        return list;
    }

    private void findFiguresRecursive(Parent p, Point2D pp, List<Figure> found, boolean decompose) {
        double tolerance = TOLERANCE / getZoomFactor();
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            Figure f1 = nodeToFigureMap.get(n);
            if (f1 != null && f1.isSelectable()) {
                Point2D pl = n.parentToLocal(pp);
                if (contains(n, pl, tolerance)) { // only drill down if the parent contains the point
                    Figure f = nodeToFigureMap.get(n);
                    if (f != null && f.isSelectable()) {
                        found.add(f);
                    }
                    if (f == null || !f.isSelectable() || decompose && f.isDecomposable()) {
                        if (n instanceof Parent) {
                            findFiguresRecursive((Parent) n, pl, found, decompose);
                        }
                    }
                }
            } else {
                Point2D pl = n.parentToLocal(pp);
                if (contains(n, pl, tolerance)) { // only drill down if the parent intersects the point
                    if (n instanceof Parent) {
                        findFiguresRecursive((Parent) n, pl, found, decompose);
                    }
                }
            }
        }
    }

    @Override
    public List<Figure> findFiguresInside(double vx, double vy, double vwidth, double vheight, boolean decompose) {
        Transform vt = getViewToWorld();
        Point2D pxy = vt.transform(vx, vy);
        Point2D pwh = vt.deltaTransform(vwidth, vheight);
        BoundingBox r = new BoundingBox(pxy.getX(), pxy.getY(), pwh.getX(), pwh.getY());
        List<Figure> list = new LinkedList<Figure>();
        findFiguresInsideRecursive((Parent) figureToNodeMap.get(getDrawing()), r, list, decompose);
        return list;
    }

    private void findFiguresInsideRecursive(Parent p, Bounds pp, List<Figure> found, boolean decompose) {
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            if (!n.isVisible()) {
                continue;
            }
            Figure f1 = nodeToFigureMap.get(n);
            if (f1 != null && f1.isSelectable() && f1.isVisible()) {
                Bounds pl = n.parentToLocal(pp);
                if (pl.contains(n.getBoundsInLocal())) { // only drill down if the parent bounds contains the point
                    Figure f = nodeToFigureMap.get(n);
                    if (f != null && f.isSelectable()) {
                        found.add(f);
                    }
                    if (f == null || !f.isSelectable() || decompose && f.isDecomposable()) {
                        if (n instanceof Parent) {
                            findFiguresInsideRecursive((Parent) n, pl, found, decompose);
                        }
                    }
                }
            } else {
                Bounds pl = n.parentToLocal(pp);
                if (n.intersects(pl)) { // only drill down if the parent intersects the point
                    if (n instanceof Parent) {
                        findFiguresInsideRecursive((Parent) n, pl, found, decompose);
                    }
                }
            }
        }
    }

    @Override
    public List<Figure> findFiguresIntersecting(double vx, double vy, double vwidth, double vheight, boolean decompose) {
        Transform vt = getViewToWorld();
        Point2D pxy = vt.transform(vx, vy);
        Point2D pwh = vt.deltaTransform(vwidth, vheight);
        BoundingBox r = new BoundingBox(pxy.getX(), pxy.getY(), pwh.getX(), pwh.getY());
        List<Figure> list = new LinkedList<Figure>();
        findFiguresIntersectingRecursive((Parent) figureToNodeMap.get(getDrawing()), r, list, decompose);
        return list;
    }

    private void findFiguresIntersectingRecursive(Parent p, Bounds pp, List<Figure> found, boolean decompose) {
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            Bounds pl = n.parentToLocal(pp);
            if (n.intersects(pl)) { // only drill down if the parent intersects the point
                Figure f = nodeToFigureMap.get(n);
                if (f != null && f.isSelectable()) {
                    found.add(f);
                }
                if (f == null || !f.isSelectable() || decompose && f.isDecomposable()) {
                    if (n instanceof Parent) {
                        findFiguresIntersectingRecursive((Parent) n, pl, found, decompose);
                    }
                }
            }
        }
    }

    @Override
    public ObjectProperty<Layer> activeLayerProperty() {
        return activeLayer;
    }

    @Override
    public DrawingModel getModel() {
        return drawingModel.get();
    }

    public void setDrawingModel(DrawingModel newValue) {
        drawingModel.set(newValue);
    }

    // Handles
    @Override
    public Set<Figure> getFiguresWithCompatibleHandle(Collection<Figure> figures, Handle master) {
        validateHandles();
        Map<Figure, Figure> result = new IdentityHashMap<>();
        for (Map.Entry<Figure, List<Handle>> entry : handles.entrySet()) {
            if (figures.contains(entry.getKey())) {
                for (Handle h : entry.getValue()) {
                    if (h.isCompatible(master)) {
                        result.put(entry.getKey(), null);
                        break;
                    }
                }
            }
        }
        return result.keySet();
    }

    /**
     * Gets the currently active selection handles. / private
     * java.util.List<Handle> getSelectionHandles() { validateHandles(); return
     * Collections.unmodifiableList(handles); }
     */
    /**
     * Gets the currently active secondary handles. / private
     * java.util.List<Handle> getSecondaryHandles() { validateHandles(); return
     * Collections.unmodifiableList(secondaryHandles); }
     */
    /**
     * Invalidates the handles.
     */
    public void invalidateHandles() {
        if (handlesAreValid) {
            handlesAreValid = false;
        }
    }

    public void recreateHandles() {
        handlesAreValid = false;
        recreateHandles = true;
        repaint();
    }

    private void invalidateHandleNodes() {
        for (Figure f : handles.keySet()) {
            dirtyHandles.add(f);
        }
        repaint();
    }

    /**
     * Validates the handles.
     */
    private void validateHandles() {
        // Validate handles only, if they are invalid/*, and if
        // the DrawingView has a DrawingEditor.*/
        if (!handlesAreValid /*
                 * && getEditor() != null
                 */) {
            handlesAreValid = true;
            updateHandles();
            updateLayout();
        }
    }

    private void updateHandles() {
        if (recreateHandles) {
            // FIXME - We create and destroy many handles here!!!
            for (Map.Entry<Figure, List<Handle>> entry : handles.entrySet()) {
                for (Handle h : entry.getValue()) {
                    h.dispose();
                }
            }
            nodeToHandleMap.clear();
            handles.clear();
            handlesPane.getChildren().clear();
            dirtyHandles.clear();

            createHandles(handles);
            recreateHandles = false;
        }

        for (Map.Entry<Figure, List<Handle>> entry : handles.entrySet()) {
            //dirtyHandles.add(entry.getKey());
            for (Handle handle : entry.getValue()) {
                Node n = handle.getNode();
                if (nodeToHandleMap.put(n, handle) == null) {
                    handlesPane.getChildren().add(n);
                }
                handle.updateNode(this);
            }
        }
    }

    /**
     * Creates selection handles and adds them to the provided list.
     *
     * @param handles The provided list
     */
    protected void createHandles(Map<Figure, List<Handle>> handles) {
        ArrayList<Figure> selection = new ArrayList<>(getSelectedFigures());
        if (selection.size()>1) {
            if (getAnchorHandleType() != null) {
                Figure anchor = selection.get(0);
                List<Handle> list = handles.computeIfAbsent(anchor, k -> new ArrayList<>());
                anchor.createHandles(getAnchorHandleType(), list);
            }
            if (getLeadHandleType() != null) {
                Figure anchor = selection.get(selection.size()-1);
                List<Handle> list = handles.computeIfAbsent(anchor, k -> new ArrayList<>());
                anchor.createHandles(getLeadHandleType(), list);
            }
        }
        HandleType handleType = getHandleType();
        for (Figure figure : selection) {
            List<Handle> list = handles.computeIfAbsent(figure, k -> new ArrayList<>());
            figure.createHandles(handleType, list);
            handles.put(figure, list);
        }
    }

    private void invalidateWorldViewTransforms() {
        worldToViewTransform = viewToWorldTransform = null;
    }

    private void invalidateConstrainerNode() {
        constrainerNodeValid = false;
    }

    /**
     * The stylesheet used for handles and tools.
     *
     * @return the stylesheet list
     */
    public ObservableList<String> overlayStylesheets() {
        return overlaysPane.getStylesheets();
    }

    /**
     * Selects all enabled and selectable figures in all enabled layers.
     */
    @Override
    public void selectAll() {
        ArrayList<Figure> figures = new ArrayList<>();
        Drawing d = getDrawing();
        if (d != null) {
            for (Figure layer : d.getChildren()) {
                if (layer.isEditable()) {
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

@Override
    public void clearSelection() {
        getSelectedFigures().clear();
    }

@Override
    public void deleteSelection() {
        ArrayList<Figure> figures = new ArrayList<>(getSelectedFigures());
        DrawingModel model = getModel();
        for (Figure f : figures) {
            if (f.isDeletable()) {
                model.disconnect(f);
                model.removeFromParent(f);
            }
        }
    }
@Override
    public void duplicateSelection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

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
}
