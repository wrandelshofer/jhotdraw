/*
 * @(#)SimpleDrawingView.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.beans.NonNullProperty;
import org.jhotdraw8.collection.ReversedList;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.css.MacOSSystemColorConverter;
import org.jhotdraw8.draw.constrain.Constrainer;
import org.jhotdraw8.draw.constrain.NullConstrainer;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.model.DrawingModelEvent;
import org.jhotdraw8.draw.model.SimpleDrawingModel;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.geom.FXGeom;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.tree.TreeBreadthFirstSpliterator;
import org.jhotdraw8.tree.TreeModelEvent;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.Math.max;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 */
public class SimpleDrawingView extends AbstractDrawingView implements EditableComponent {

    /**
     * The id of the canvas pane for CSS styling.
     */
    public static final String CANVAS_PANE_ID = "canvasPane";

    private final static double INVSQRT2 = 1.0 / Math.sqrt(2);
    /**
     * The name of the margin property.
     */
    public final static String MARGIN_PROPERTY = "margin";
    /**
     * The id of the tool pane for CSS styling.
     */
    public static final String TOOL_PANE_ID = "toolPane";
    private final ObjectProperty<Figure> activeParent = new SimpleObjectProperty<>(this, ACTIVE_PARENT_PROPERTY);

    private Rectangle canvasPane;
    /**
     * The constrainer property holds the constrainer for this drawing view
     */
    private final NonNullProperty<Constrainer> constrainer = new NonNullProperty<>(this, CONSTRAINER_PROPERTY, new NullConstrainer());
    private boolean constrainerNodeValid;
    /**
     * This is the set of figures which are out of sync with their JavaFX node.
     */
    private final Set<Figure> dirtyFigureNodes = new HashSet<>();
    /**
     * This is the set of handles which are out of sync with their JavaFX node.
     */
    private final Set<Figure> dirtyHandles = new HashSet<>();
    private final ReadOnlyObjectWrapper<Drawing> drawing = new ReadOnlyObjectWrapper<>(this, DRAWING_PROPERTY);

    /**
     * The drawingProperty holds the drawing that is presented by this drawing
     * view.
     */
    @Nullable
    private final NonNullProperty<DrawingModel> drawingModel //
            = new NonNullProperty<DrawingModel>(this, MODEL_PROPERTY, new SimpleDrawingModel()) {
        @Nullable
        private DrawingModel oldValue = null;

        @Override
        protected void fireValueChangedEvent() {
            DrawingModel newValue = get();
            super.fireValueChangedEvent();
            onNewDrawingModel(oldValue, newValue);
            oldValue = newValue;
        }
    };
    private final Listener<DrawingModelEvent> drawingModelHandler = event -> {
        Figure f = event.getNode();
        switch (event.getEventType()) {
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
            case PROPERTY_VALUE_CHANGED:
            case TRANSFORM_CHANGED:
                break;
            default:
                throw new UnsupportedOperationException(event.getEventType()
                        + " not supported");
        }
    };
    private Group drawingPane;

    private Group drawingSubScene;
    /**
     * Maps JavaFX nodes to a figure. Note that the DrawingView may contain
     * JavaFX nodes which have no mapping. this is usually the case, when a
     * Figure is represented by multiple nodes. Then only the parent of these
     * nodes is associated with the figure.
     */
    private final Map<Figure, Node> figureToNodeMap = new HashMap<>();
    /**
     * This is just a wrapper around the focusedProperty of the JavaFX Node
     * which is used to render this view.
     */
    private final ReadOnlyBooleanWrapper focused = new ReadOnlyBooleanWrapper(this, FOCUSED_PROPERTY);
    private Group gridPane;
    /**
     * The set of all handles which were produced by selected figures.
     */
    private final Map<Figure, List<Handle>> handles = new LinkedHashMap<>();
    private boolean handlesAreValid;
    private Group handlesPane;
    /**
     * Margin around the drawing.
     */
    private final ObjectProperty<Insets> margin = new NonNullProperty<>(this, MARGIN_PROPERTY, new Insets(20, 20, 20, 20));

    private final InvalidationListener modelInvalidationListener = o -> repaint();

    private SimpleDrawingViewNode node;
    /**
     * Maps each JavaFX node to a figure in the drawing.
     */
    private final Map<Node, Figure> nodeToFigureMap = new HashMap<>();
    /**
     * Maps each JavaFX node to a handle in the drawing view.
     */
    private final Map<Node, Handle> nodeToHandleMap = new LinkedHashMap<>();
    private Pane overlaysPane;
    private Group overlaysSubScene;
    @Nullable
    private Bounds previousScaledBounds = null;
    private boolean recreateHandles;
    boolean renderIntoImage = false;

    @Nullable
    private Runnable repainter = null;
    /**
     * This is the JavaFX Node which is used to represent this drawing view. in
     * a JavaFX scene graph.
     */
    private Pane rootPane;
    private ScrollPane scrollPane;
    /**
     * The set of all secondary handles. One handle at a time may create
     * secondary handles.
     */
    private final ArrayList<Handle> secondaryHandles = new ArrayList<>();
    /**
     * If too many figures are selected, we only draw one outline handle for all
     * selected figures instead of one outline handle for each selected figure.
     */
    private int tooManySelectedFigures = 20;
    private BorderPane toolPane;
    private final Listener<TreeModelEvent<Figure>> treeModelHandler = (TreeModelEvent<Figure> event) -> {
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
                onDrawingChanged();
                updateLayout();
                repaint();
                break;
            case SUBTREE_NODES_CHANGED:
                onSubtreeNodesChanged(f);
                repaint();
                break;
            default:
                throw new UnsupportedOperationException(event.getEventType()
                        + " not supported");
        }
    };
    @Nullable
    private Transform viewToWorldTransform = null;
    private final InvalidationListener visibleRectChangedHandler = this::onVisibleRectChanged;
    @Nullable
    private Transform worldToViewTransform = null;
    /**
     * The zoom factor.
     */
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, ZOOM_FACTOR_PROPERTY, 1.0);

    {
        zoomFactor.addListener(this::onZoomFactorChanged);
    }

    {
        margin.addListener(observable -> updateLayout());
    }

    {
        constrainer.addListener((o, oldValue, newValue) -> updateConstrainer(oldValue, newValue));
    }

    public SimpleDrawingView() {
        init();
    }

    @NonNull
    @Override
    public ObjectProperty<Figure> activeParentProperty() {
        return activeParent;
    }

    private void clearNodes() {
        figureToNodeMap.clear();
        nodeToFigureMap.clear();
        dirtyFigureNodes.clear();
    }

    @Override
    public void clearSelection() {
        getSelectedFigures().clear();
    }

    @NonNull
    @Override
    public NonNullProperty<Constrainer> constrainerProperty() {
        return constrainer;
    }

    /**
     * Returns true if the node contains the specified point within a a
     * tolerance.
     *
     * @param node      The node
     * @param point     The point in local coordinates
     * @param tolerance The maximal distance the point is allowed to be away
     *                  from the node
     * @return true if the node contains the point
     */
    private boolean contains(@NonNull Node node, @NonNull Point2D point, double tolerance) {
        double toleranceInLocal = tolerance / node.getLocalToSceneTransform().deltaTransform(1, 1).magnitude();

        if (node instanceof Shape) {
            Shape shape = (Shape) node;
            if (shape.contains(point)) {
                return true;
            }

            double widthFactor;
            switch (shape.getStrokeType()) {
                case CENTERED:
                default:
                    widthFactor = 0.5;
                    break;
                case INSIDE:
                    widthFactor = 0;
                    break;
                case OUTSIDE:
                    widthFactor = 1;
                    break;
            }
            if (FXGeom.grow(shape.getBoundsInParent(), tolerance, tolerance).contains(point)) {
                return Shapes.outlineContains(Shapes.awtShapeFromFX(shape), new java.awt.geom.Point2D.Double(point.getX(), point.getY()),
                        shape.getStrokeWidth() * widthFactor + toleranceInLocal);
            } else {
                return false;
            }
        } else if (node instanceof Group) {
            if (FXGeom.contains(node.getBoundsInLocal(), point, toleranceInLocal)) {
                for (Node child : ((Group) node).getChildren()) {
                    if (contains(child, child.parentToLocal(point), tolerance)) {
                        return true;
                    }
                }
            }
            return false;
        } else { // foolishly assumes that all other nodes are rectangular and opaque
            return FXGeom.contains(node.getBoundsInLocal(), point, tolerance);
        }
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

    /**
     * Creates selection handles and adds them to the provided list.
     *
     * @param handles The provided list
     */
    protected void createHandles(@NonNull Map<Figure, List<Handle>> handles) {
        List<Figure> selection = new ArrayList<>(getSelectedFigures());
        if (selection.size() > 1) {
            if (getEditor().getAnchorHandleType() != null) {
                Figure anchor = selection.get(0);
                List<Handle> list = handles.computeIfAbsent(anchor, k -> new ArrayList<>());
                anchor.createHandles(getEditor().getAnchorHandleType(), list);
            }
            if (getEditor().getLeadHandleType() != null) {
                Figure anchor = selection.get(selection.size() - 1);
                List<Handle> list = handles.computeIfAbsent(anchor, k -> new ArrayList<>());
                anchor.createHandles(getEditor().getLeadHandleType(), list);
            }
        }
        HandleType handleType = getEditor().getHandleType();
        ArrayList<Handle> list = new ArrayList<>();
        for (Figure figure : selection) {
            figure.createHandles(handleType, list);
        }
        for (Handle h : list) {
            Figure figure = h.getOwner();
            handles.computeIfAbsent(figure, k -> new ArrayList<>()).add(h);
        }
    }

    @Override
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

    @Override
    public ReadOnlyObjectProperty<Drawing> drawingProperty() {
        return drawing.getReadOnlyProperty();
    }

    @Override
    public void duplicateSelection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Nullable
    @Override
    public Figure findFigure(double vx, double vy) {
        Drawing dr = getDrawing();
        Figure f = findFigureRecursive((Parent) getNode(dr), viewToWorld(vx, vy),
                getViewToWorld().deltaTransform(getEditor().getTolerance(), getEditor().getTolerance()).getX());
        return f;
    }

    /**
     * Finds a figure at the specified coordinate, but looks only at figures in
     * the specified set.
     * <p>
     * Uses a default tolerance value. See {@link #findFigure(double, double, java.util.Set, double)
     * }.
     *
     * @param vx      point in view coordinates
     * @param vy      point in view coordinates
     * @param figures figures of interest
     * @return a figure in the specified set which contains the point, or null.
     */
    @Nullable
    @Override
    public Figure findFigure(double vx, double vy, @NonNull Set<Figure> figures) {
        return findFigure(vx, vy, figures, getEditor().getTolerance());
    }

    /**
     * Finds a figure at the specified coordinate, but looks only at figures in
     * the specified set.
     *
     * @param vx        point in view coordinates
     * @param vy        point in view coordinates
     * @param figures   figures of interest
     * @param tolerance the number of pixels around the figure in view
     *                  coordinates, in which the the point is considered to be inside the figure
     * @return a figure in the specified set which contains the point, or null.
     */
    @Nullable
    public Figure findFigure(double vx, double vy, @NonNull Set<Figure> figures, double tolerance) {
        Node worldNode = getNode(getDrawing());
        Point2D pointInScene = worldNode.getLocalToSceneTransform().transform(viewToWorld(vx, vy));
        for (Figure f : figures) {
            if (f.isShowing()) {
                Node n = getNode(f);
                Point2D pointInLocal = n.sceneToLocal(pointInScene);
                if (contains(n, pointInLocal, tolerance)) {
                    return f;
                }
            }
        }

        return null;
    }

    @Nullable
    private Figure findFigureRecursive(@Nullable Parent p, @NonNull Point2D pp, double tolerance) {
        if (p == null) {
            return null;
        }
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

    @NonNull
    @Override
    public List<Figure> findFigures(double vx, double vy, boolean decompose) {
        Transform vt = getViewToWorld();
        Point2D pp = vt.transform(vx, vy);
        List<Figure> list = new ArrayList<>();
        findFiguresRecursive((Parent) figureToNodeMap.get(getDrawing()), pp, list, decompose);
        return list;
    }

    @Override
    public @Nullable Node findFigureNode(@NonNull Figure figure, double vx, double vy) {

        Node n = figureToNodeMap.get(figure);
        Transform viewToNode = null;
        for (Node p = n; p != null; p = p.getParent()) {
            try {
                viewToNode = FXTransforms.concat(viewToNode, p.getLocalToParentTransform().createInverse());
            } catch (NonInvertibleTransformException e) {
                return null;
            }
            if (p == drawingPane) {
                break;
            }
        }
        Point2D pl = FXTransforms.transform(viewToNode, vx, vy);
        return findFigureNodeRecursive(figure, n, pl.getX(), pl.getY());
    }

    private @Nullable Node findFigureNodeRecursive(@NonNull Figure figure, @NonNull Node n, double vx, double vy) {
        if (n.contains(vx, vy)) {
            if (n instanceof Shape) {
                return n;
            } else if (n instanceof Group) {
                Point2D pl = n.parentToLocal(vx, vy);
                Group group = (Group) n;
                ObservableList<Node> children = group.getChildren();
                for (int i = children.size() - 1; i >= 0; i--) {// front to back
                    Node child = children.get(i);
                    Node found = findFigureNodeRecursive(figure, child, pl.getX(), pl.getY());
                    if (found != null) {
                        return found;
                    }
                }

            }
        }
        return null;
    }

    @NonNull
    @Override
    public List<Figure> findFiguresInside(double vx, double vy, double vwidth, double vheight, boolean decompose) {
        Transform vt = getViewToWorld();
        Point2D pxy = vt.transform(vx, vy);
        Point2D pwh = vt.deltaTransform(vwidth, vheight);
        BoundingBox r = new BoundingBox(pxy.getX(), pxy.getY(), pwh.getX(), pwh.getY());
        List<Figure> list = new ArrayList<>();
        findFiguresInsideRecursive((Parent) figureToNodeMap.get(getDrawing()), r, list, decompose);
        return list;
    }

    private void findFiguresInsideRecursive(@NonNull Parent p, @NonNull Bounds pp, @NonNull List<Figure> found, boolean decompose) {
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            if (!n.isVisible()) {
                continue;
            }
            Figure f1 = nodeToFigureMap.get(n);
            if (f1 != null && f1.isSelectable() && f1.isShowing()) {
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

    @NonNull
    @Override
    public List<Figure> findFiguresIntersecting(double vx, double vy, double vwidth, double vheight, boolean decompose) {
        Transform vt = getViewToWorld();
        Point2D pxy = vt.transform(vx, vy);
        Point2D pwh = vt.deltaTransform(vwidth, vheight);
        BoundingBox r = new BoundingBox(pxy.getX(), pxy.getY(), pwh.getX(), pwh.getY());
        List<Figure> list = new ArrayList<>();
        findFiguresIntersectingRecursive((Parent) figureToNodeMap.get(getDrawing()), r, list, decompose);
        return list;
    }

    private void findFiguresIntersectingRecursive(@NonNull Parent p, @NonNull Bounds pp, @NonNull List<Figure> found, boolean decompose) {
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

    private void findFiguresRecursive(@NonNull Parent p, @NonNull Point2D pp, @NonNull List<Figure> found, boolean decompose) {
        double tolerance = getEditor().getTolerance();
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            Figure f1 = nodeToFigureMap.get(n);
            if (f1 != null && f1.isSelectable()) {
                Point2D pl = n.parentToLocal(pp);
                if (contains(n, pl, tolerance)) { // only drill down if the parent contains the point
                    Figure f = nodeToFigureMap.get(n);
                    if (f != null && f.isSelectable() && f1.isShowing()) {
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

    @Nullable
    @Override
    public Handle findHandle(double vx, double vy) {
        if (recreateHandles) {
            return null;
        }
            final double tolerance = getEditor().getTolerance();
        for (Map.Entry<Node, Handle> e : new ReversedList<>(nodeToHandleMap.entrySet())) {
            final Node node = e.getKey();
            final Handle handle = e.getValue();
            if (!handle.isSelectable()) {
                continue;
            }
            if (handle.contains(this, vx, vy, tolerance)) {
                return handle;
            } else {
                if (false) {
                    if (contains(node, new Point2D(vx, vy), tolerance)) {
                        return handle;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ReadOnlyBooleanProperty focusedProperty() {
        return focused.getReadOnlyProperty();
    }

    public void setDrawingModel(DrawingModel newValue) {
        drawingModel.set(newValue);
    }

    // Handles
    @NonNull
    @Override
    public Set<Figure> getFiguresWithCompatibleHandle(@NonNull Collection<Figure> figures, Handle master) {
        validateHandles();
        Map<Figure, Figure> result = new HashMap<>();
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

    public Insets getMargin() {
        return margin.get();
    }

    public void setMargin(Insets value) {
        margin.set(value);
    }

    @Override
    public DrawingModel getModel() {
        return drawingModel.get();
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Nullable
    @Override
    public Node getNode(@Nullable Figure f) {
        if (f == null) {
            return null;
        }
        Node n = figureToNodeMap.get(f);
        if (n == null) {
            n = f.createNode(this);
            figureToNodeMap.put(f, n);
            nodeToFigureMap.put(n, f);
            dirtyFigureNodes.add(f);
            repaint();
        }
        return n;
    }

    private ScrollPane getScrollPane() {
        return scrollPane;
    }

    public ObservableList<String> getStylesheets() {
        return rootPane.getStylesheets();
    }

    @NonNull
    @Override
    public Transform getViewToWorld() {
        if (viewToWorldTransform == null) {
            // We try to avoid the Scale transform as it is slower than a Translate transform
            Transform tr = new Translate(-drawingPane.getTranslateX() + overlaysPane.getTranslateX(), -drawingPane.getTranslateY() + overlaysPane.getTranslateX());
            double zoom = zoomFactor.get();
            viewToWorldTransform = (zoom == 1.0) ? tr : FXTransforms.concat(new Scale(1.0 / zoom, 1.0 / zoom), tr);
        }
        return viewToWorldTransform;
    }

    @Override
    public Bounds getVisibleRect() {
        ScrollPane sp = getScrollPane();
        if (sp == null) {
            return getNode().getBoundsInLocal();
        }

        final Bounds viewportBounds = sp.getViewportBounds();

        final Bounds contentBounds = sp.getContent().getBoundsInLocal();

        final double hmin = sp.getHmin();
        final double hmax = sp.getHmax();
        final double hvalue = sp.getHvalue();
        final double contentWidth = contentBounds.getWidth();
        final double viewportWidth = viewportBounds.getWidth();

        final double vmin = sp.getVmin();
        final double vmax = sp.getVmax();
        final double vvalue = sp.getVvalue();
        final double contentHeight = contentBounds.getHeight();
        final double viewportHeight = viewportBounds.getHeight();

        final double hoffset = Math.max(0, contentWidth - viewportWidth) * (hvalue - hmin) / (hmax - hmin);
        final double voffset = Math.max(0, contentHeight - viewportHeight) * (vvalue - vmin) / (vmax - vmin);

        final Bounds rect = new BoundingBox(hoffset, voffset, viewportWidth, viewportHeight);

        return rect;
    }

    @NonNull
    @Override
    public Transform getWorldToView() {
        if (worldToViewTransform == null) {
            // We try to avoid the Scale transform as it is slower than a Translate transform
            Transform tr = new Translate(drawingPane.getTranslateX() - overlaysPane.getTranslateX(), drawingPane.getTranslateY() - overlaysPane.getTranslateX());
            double zoom = zoomFactor.get();
            worldToViewTransform = (zoom == 1.0) ? tr : FXTransforms.concat(tr, new Scale(zoom, zoom));
        }
        return worldToViewTransform;
    }

    private void onConstrainerInvalidated(Observable o) {
        invalidateConstrainerNode();
        repaint();
    }

    private void onDrawingChanged() {
        clearNodes();
        clearSelection();
        drawingPane.getChildren().clear();
        activeParent.set(null);
        Drawing d = getModel().getDrawing();
        drawing.set(d);
        if (d != null) {
            drawingPane.getChildren().add(getNode(d));
            dirtyFigureNodes.add(d);
            updateLayout();
            //handleSubtreeNodesChanged(d);
            repaint();

            for (int i = d.getChildren().size() - 1; i >= 0; i--) {
                Figure child = d.getChild(i);
                if (child instanceof Layer) {
                    Layer layer = (Layer) child;
                    if (!layer.isEditable() && layer.isShowing()) {
                        activeParent.set(layer);
                        break;
                    }
                } else {
                    activeParent.set(d);
                }

            }
        }
        invalidateConstrainerNode();
    }

    private void onFigureAdded(@NonNull Figure figure) {
        for (Figure f : figure.preorderIterable()) {
            invalidateFigureNode(f);
        }
        repaint();
    }

    private void onFigureRemoved(@NonNull Figure figure) {
        for (Figure f : figure.preorderIterable()) {
            removeNode(f);
        }
        invalidateHandles();
        repaint();
    }

    private void onFigureRemovedFromDrawing(@NonNull Figure figure) {
        final ObservableSet<Figure> selectedFigures = getSelectedFigures();
        for (Figure f : figure.preorderIterable()) {
            selectedFigures.remove(f);
        }
    }

    private void onNewDrawingModel(@Nullable DrawingModel oldValue, @Nullable DrawingModel newValue) {
        if (oldValue != null) {
            clearSelection();
            oldValue.removeTreeModelListener(treeModelHandler);
            oldValue.removeDrawingModelListener(drawingModelHandler);
            oldValue.removeListener(modelInvalidationListener);
            drawing.setValue(null);
        }
        if (newValue != null) {
            newValue.addDrawingModelListener(drawingModelHandler);
            newValue.addTreeModelListener(treeModelHandler);
            newValue.addListener(modelInvalidationListener);
            onDrawingChanged();
            updateLayout();
            repaint();
        }
    }

    private void onNodeChanged(Figure f) {
        invalidateFigureNode(f);
        if (f == getDrawing()) {
            updateLayout();
            if (constrainer.get() != null) {
                onConstrainerInvalidated(constrainer.get());
            }
        }
        repaint();
    }

    private void onSubtreeNodesChanged(@NonNull Figure figures) {
        for (Figure f : figures.preorderIterable()) {
            dirtyFigureNodes.add(f);
            dirtyHandles.add(f);
        }
    }

    private void onVisibleRectChanged(Observable o) {
        invalidateConstrainerNode();
        invalidateLayerNodes();
        invalidateHandles();
        repaint();
    }

    private void onZoomFactorChanged(ObservableValue<? extends Number> observable, Number oldValue, @NonNull Number newValue) {
        final Bounds visibleRect = getViewToWorld().transform(getVisibleRect());

        Scale st = new Scale(newValue.doubleValue(), newValue.doubleValue());
        if (drawingPane != null) {
            if (drawingPane.getTransforms().isEmpty()) {
                drawingPane.getTransforms().add(st);
            } else {
                drawingPane.getTransforms().set(0, st);
            }
        }
        updateLayout();
        invalidateFigureNodes();
        invalidateHandleNodes();
        if (constrainer.get() != null) {
            constrainer.get().updateNode(SimpleDrawingView.this);
        }

        // We have to wait until the scroll pane has finished layouting.
        CompletableFuture.runAsync(() -> {
        }, Platform::runLater)
                .thenRunAsync(() -> scrollRectToVisible(getWorldToView().transform(visibleRect)), Platform::runLater);
    }

    private boolean hasNode(Figure f) {
        return figureToNodeMap.containsKey(f);
    }

    protected void init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            rootPane = loader.load(SimpleDrawingView.class.getResourceAsStream("SimpleDrawingView.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        canvasPane = new Rectangle();
        canvasPane.setId(CANVAS_PANE_ID);
        canvasPane.setFill(new ImagePattern(createCheckerboardImage(Color.WHITE, Color.LIGHTGRAY, 8), 0, 0, 16, 16, false));

        drawingSubScene = new Group();
        drawingSubScene.setManaged(false);
        drawingSubScene.setMouseTransparent(true);
        overlaysSubScene = new Group();
        overlaysSubScene.setManaged(false);
        drawingPane = new Group();
        drawingSubScene.getChildren().addAll(canvasPane, drawingPane);

        toolPane = new BorderPane();
        toolPane.setId(TOOL_PANE_ID);
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
        rootPane.getChildren().addAll(drawingSubScene, overlaysSubScene);

        // We use a change listener instead of an invalidation listener here,
        // because we only want to update the layout, when the new value is
        // different from the old value!
        drawingPane.layoutBoundsProperty().addListener(observer -> updateLayout());

        drawingModel.get().setRoot(new SimpleDrawing());
        onNewDrawingModel(null, drawingModel.get());

        // Set stylesheet
        rootPane.getStylesheets().add(SimpleDrawingView.class.getResource("SimpleDrawingView.css").toString());

        // set root
        node = new SimpleDrawingViewNode();
        node.setCenter(rootPane);

        // install/deiinstall listeners to scrollpane
        node.sceneProperty().addListener(this::updateScrollPaneListeners);

        set(SYSTEM_COLOR_CONVERTER_KEY, new MacOSSystemColorConverter());
    }

    private void invalidateConstrainerNode() {
        constrainerNodeValid = false;
    }

    private void invalidateFigureNode(Figure f) {
        dirtyFigureNodes.add(f);
        if (handles.containsKey(f)) {
            dirtyHandles.add(f);
        }
    }

    private void invalidateFigureNodes() {
        dirtyFigureNodes.addAll(nodeToFigureMap.values());
    }

    private void invalidateHandleNodes() {
        for (Figure f : handles.keySet()) {
            dirtyHandles.add(f);
        }
        repaint();
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
    @Override
    public void invalidateHandles() {
        if (handlesAreValid) {
            handlesAreValid = false;
        }
    }

    private void invalidateLayerNodes() {
        for (Figure f : getDrawing().getChildren()) {
            dirtyFigureNodes.add(f);
        }
    }

    private void invalidateWorldViewTransforms() {
        worldToViewTransform = viewToWorldTransform = null;
    }

    /**
     * Returns true if the point is inside the radius from the center of the
     * node.
     *
     * @param node          The node
     * @param point         The point in local coordinates
     * @param squaredRadius The square of the radius in which the node must be
     * @return true if the node contains the point
     */
    private boolean isInsideRadius(@NonNull Node node, @NonNull Point2D point, double squaredRadius) {
        Bounds b = node.getBoundsInLocal();
        double cx = b.getMinX() + b.getWidth() * 0.5;
        double cy = b.getMinY() + b.getHeight() * 0.5;
        double dx = point.getX() - cx;
        double dy = point.getY() - cy;
        return dx * dx + dy * dy < squaredRadius;
    }

    /**
     * The top, right, bottom, and left margin around the drawing.
     *
     * @return The margin. The default value is:
     * {@code new Insets(20, 20, 20, 20)}.
     */
    @NonNull
    public ObjectProperty<Insets> marginProperty() {
        return margin;
    }

    @Nullable
    @Override
    public NonNullProperty<DrawingModel> modelProperty() {
        return drawingModel;
    }

    /**
     * The stylesheet used for handles and tools.
     *
     * @return the stylesheet list
     */
    public ObservableList<String> overlayStylesheets() {
        return overlaysPane.getStylesheets();
    }

    @Override
    public void recreateHandles() {
        handlesAreValid = false;
        recreateHandles = true;
        repaint();
    }

    @Override
    public void jiggleHandles() {
        validateHandles();
        List<Handle> copiedList = handles.values().stream().flatMap(List::stream).collect(Collectors.toList());

        // We scale the handles back and forth.
        double amount = 0.1;
        Transition flash = new Transition() {
            {
                setCycleDuration(Duration.millis(100));
                setCycleCount(2);
                setAutoReverse(true);
            }

            @Override
            protected void interpolate(double frac) {
                for (Handle h : copiedList) {
                    Node node = h.getNode(SimpleDrawingView.this);
                    node.setScaleX(1 + frac * amount);
                    node.setScaleY(1 + frac * amount);
                }
            }
        };
        flash.play();
    }

    private void removeNode(Figure f) {
        Node oldNode = figureToNodeMap.remove(f);
        if (oldNode != null) {
            nodeToFigureMap.remove(oldNode);
        }
        dirtyFigureNodes.remove(f);
    }

    /**
     * Repaints the view.
     */
    public void repaint() {
        if (repainter == null) {
            repainter = () -> {
                updateRenderContext();
                getModel().validate(this);
                repainter = null;
                updateNodes();
                validateHandles();
                //dump(getNode(getDrawing()),0);
            };
            Platform.runLater(repainter);
        }
    }

    @Override
    public void scrollRectToVisible(@NonNull Bounds boundsInView) {
        ScrollPane sp = getScrollPane();
        if (sp == null) {
            return;
        }

        final Bounds contentBounds = sp.getContent().getBoundsInLocal();
        double width = contentBounds.getWidth();
        double height = contentBounds.getHeight();
        double x = boundsInView.getMinX() + boundsInView.getWidth() * 0.5;
        double y = boundsInView.getMinY() + boundsInView.getHeight() * 0.5;

        // scrolling values range from 0 to 1
        Bounds viewportBounds = sp.getViewportBounds();
        sp.setVvalue(Geom.clamp((y - viewportBounds.getHeight() * 0.5) / (height - viewportBounds.getHeight()), 0.0, 1.0));
        sp.setHvalue(Geom.clamp((x - viewportBounds.getWidth() * 0.5) / (width - viewportBounds.getWidth()), 0.0, 1.0));
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

    @Override
    public ReadOnlyBooleanProperty selectionEmptyProperty() {
        return selectedFiguresProperty().emptyProperty();
    }

    private void updateConstrainer(@Nullable Constrainer oldValue, @Nullable Constrainer newValue) {
        if (oldValue != null) {
            gridPane.getChildren().remove(oldValue.getNode());
            oldValue.removeListener(this::onConstrainerInvalidated);
        }
        if (newValue != null) {
            gridPane.getChildren().add(newValue.getNode());
            newValue.getNode().applyCss();
            newValue.updateNode(this);
            newValue.addListener(this::onConstrainerInvalidated);
            invalidateConstrainerNode();
            repaint();
        }
    }

    private void updateConstrainerNode() {
        constrainerNodeValid = true;
        Constrainer c = getConstrainer();
        if (c != null) {
            c.updateNode(this);
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

        Bounds visibleRect = getVisibleRect();

        for (Map.Entry<Figure, List<Handle>> entry : handles.entrySet()) {
            //dirtyHandles.addChild(entry.getKey());
            for (Handle handle : entry.getValue()) {
                Node n = handle.getNode(this);
                handle.updateNode(this);
                if (visibleRect.intersects(n.getBoundsInParent())) {
                    if (nodeToHandleMap.put(n, handle) == null) {
                        handlesPane.getChildren().add(n);
                    }
                }
            }
        }
    }

    /**
     * Updates the layout of the drawing pane and the panes laid over it.
     */
    private void updateLayout() {
        if (node == null) {
            return;
        }

        double f = getZoomFactor();
        Drawing d = getDrawing();
        if (d == null) {
            return;
        }
        CssSize cssWidth = d.get(Drawing.WIDTH);
        CssSize cssHeight = d.get(Drawing.HEIGHT);
        double dw = cssWidth == null ? 0.0 : cssWidth.getConvertedValue();
        double dh = cssHeight == null ? 0.0 : cssHeight.getConvertedValue();

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

        if (d != null) {
            canvasPane.setTranslateX(max(0, -x));
            canvasPane.setTranslateY(max(0, -y));
            canvasPane.setWidth(dw * f);
            canvasPane.setHeight(dh * f);
        }

        final Insets margin = getMargin();
        final double marginL = margin.getLeft();
        final double marginR = margin.getRight();
        final double marginT = margin.getTop();
        final double marginB = margin.getBottom();

        double lw = max(max(0, x) + w, max(0, -x) + dw * f);
        double lh = max(max(0, y) + h, max(0, -y) + dh * f);

        drawingPane.setTranslateX(marginL);
        drawingPane.setTranslateY(marginT);
        canvasPane.setTranslateX(marginL);
        canvasPane.setTranslateY(marginT);
        toolPane.resize(lw + marginL + marginR, lh + marginT + marginB);
        toolPane.layout();

        overlaysPane.setClip(new Rectangle(0, 0, lw + marginL + marginR, lh + marginT + marginB));

// drawingPane.setClip(new Rectangle(0,0,lw,lh));
        rootPane.setPrefSize(lw + marginL + marginR, lh + marginT + marginB);
        rootPane.setMaxSize(lw + marginL + marginR, lh + marginT + marginB);

        invalidateWorldViewTransforms();
        invalidateHandleNodes();
    }

    private void updateRenderContext() {
        set(RenderContext.CLIP_BOUNDS, viewToWorld(getVisibleRect()));
        DefaultUnitConverter units = new DefaultUnitConverter(90, 1.0, 1024.0 / getZoomFactor(), 768 / getZoomFactor());
        set(RenderContext.UNIT_CONVERTER_KEY, units);
    }

    private void updateNodes() {
        if (!renderIntoImage) {
            // create copies of the lists to allow for concurrent modification
            Figure[] copyOfDirtyFigureNodes = dirtyFigureNodes.toArray(new Figure[0]);
            dirtyFigureNodes.clear();
            for (Figure f : copyOfDirtyFigureNodes) {
                if (!f.isShowing() && !hasNode(f)) {
                    continue;
                }
                Node node = getNode(f);
                if (node != null) {
                    f.updateNode(this, node);
                }
            }
        }

        if (!recreateHandles) {
            Figure[] copyOfDirtyHandles = dirtyHandles.toArray(new Figure[0]);
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
            updateConstrainerNode();
        }
    }

    private void updateScrollPaneListeners(Observable o) {
        if (scrollPane != null) {
            scrollPane.vvalueProperty().removeListener(visibleRectChangedHandler);
            scrollPane.hvalueProperty().removeListener(visibleRectChangedHandler);
            scrollPane.widthProperty().removeListener(visibleRectChangedHandler);
            scrollPane.heightProperty().removeListener(visibleRectChangedHandler);
        }

        for (Parent p = node.getParent(); p != null; p = p.getParent()) {
            if (p instanceof ScrollPane) {
                scrollPane = (ScrollPane) p;
                break;
            }
        }
        if (scrollPane != null) {
            scrollPane.vvalueProperty().addListener(visibleRectChangedHandler);
            scrollPane.hvalueProperty().addListener(visibleRectChangedHandler);
            scrollPane.widthProperty().addListener(visibleRectChangedHandler);
            scrollPane.heightProperty().addListener(visibleRectChangedHandler);
        }
    }

    @Override
    protected void updateTool(@Nullable Tool oldValue, @Nullable Tool newValue) {
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

    private void updateTreeStructure(@NonNull Figure parent) {
        // Since we don't know which figures have been removed from
        // the drawing, we have to get rid of them on ourselves.
        // XXX This is a really slow operation. If each figure would store a
        // reference to its drawing it would perform better.
        Drawing d = getDrawing();
        for (Figure f : new ArrayList<>(figureToNodeMap.keySet())) {
            if (f.getRoot() != d) {
                removeNode(f);
            }
        }
        onSubtreeNodesChanged(parent);
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

    @NonNull
    @Override
    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

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

}
