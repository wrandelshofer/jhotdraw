/* @(#)SimpleDrawingView.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

import java.io.IOException;
import static java.lang.Math.max;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.beans.NonnullProperty;
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
import static org.jhotdraw8.draw.model.DrawingModelEvent.EventType.LAYOUT_CHANGED;
import static org.jhotdraw8.draw.model.DrawingModelEvent.EventType.LAYOUT_SUBJECT_CHANGED;
import static org.jhotdraw8.draw.model.DrawingModelEvent.EventType.PROPERTY_VALUE_CHANGED;
import static org.jhotdraw8.draw.model.DrawingModelEvent.EventType.STYLE_CHANGED;
import static org.jhotdraw8.draw.model.DrawingModelEvent.EventType.TRANSFORM_CHANGED;
import org.jhotdraw8.draw.model.SimpleDrawingModel;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;
import org.jhotdraw8.tree.TreeModelEvent;
import static org.jhotdraw8.tree.TreeModelEvent.EventType.NODE_ADDED_TO_PARENT;
import static org.jhotdraw8.tree.TreeModelEvent.EventType.NODE_ADDED_TO_TREE;
import static org.jhotdraw8.tree.TreeModelEvent.EventType.NODE_CHANGED;
import static org.jhotdraw8.tree.TreeModelEvent.EventType.NODE_REMOVED_FROM_PARENT;
import static org.jhotdraw8.tree.TreeModelEvent.EventType.NODE_REMOVED_FROM_TREE;
import static org.jhotdraw8.tree.TreeModelEvent.EventType.ROOT_CHANGED;
import static org.jhotdraw8.tree.TreeModelEvent.EventType.SUBTREE_NODES_CHANGED;
import org.jhotdraw8.util.ReversedList;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawingView extends AbstractDrawingView implements EditableComponent {

    private final static double INVSQRT2 = 1.0 / Math.sqrt(2);
    /**
     * Selection tolerance. Selectable margin around a figure.
     */
    public final static double TOLERANCE = 5;

    public final static double TOLERANCE_SQUARED = TOLERANCE * TOLERANCE;
    private final ObjectProperty<Layer> activeLayer = new SimpleObjectProperty<>(this, ACTIVE_LAYER_PROPERTY);

    private Rectangle canvasPane;
    /**
     * The constrainer property holds the constrainer for this drawing view
     */
    private final NonnullProperty<Constrainer> constrainer = new NonnullProperty<>(this, CONSTRAINER_PROPERTY, new NullConstrainer());
    private boolean constrainerNodeValid;
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
    private final ReadOnlyObjectWrapper<Drawing> drawing = new ReadOnlyObjectWrapper<>(this, DRAWING_PROPERTY);

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
    private Group drawingPane;

    private Group drawingSubScene;
    /**
     * Maps JavaFX nodes to a figure. Note that the DrawingView may contain
     * JavaFX nodes which have no mapping. this is usually the case, when a
     * Figure is represented by multiple nodes. Then only the parent of these
     * nodes is associated with the figure.
     */
    private final Map<Figure, Node> figureToNodeMap = new IdentityHashMap<>();
    /**
     * This is just a wrapper around the focusedProperty of the JavaFX Node
     * which is used to render this view.
     */
    private final ReadOnlyBooleanWrapper focused = new ReadOnlyBooleanWrapper(this, FOCUSED_PROPERTY);
    private Group gridPane;
    /**
     * The set of all handles which were produced by selected figures.
     */
    private final Map<Figure, List<Handle>> handles = new IdentityHashMap<>();
    private boolean handlesAreValid;
    private Group handlesPane;
    /**
     * The number of nodes that are maximally updated per frame.
     */
    private int maxUpdate = 100;
    private final Listener<DrawingModelEvent> drawingModelHandler = new Listener<DrawingModelEvent>() {

        @Override
        public void handle(DrawingModelEvent event) {
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
            case LAYOUT_SUBJECT_CHANGED:
            case TRANSFORM_CHANGED:
                break;
            default:
                throw new UnsupportedOperationException(event.getEventType()
                        + " not supported");
            }
        }

    };
    private final Listener<TreeModelEvent<Figure>> treeModelHandler = (TreeModelEvent<Figure> event) -> {
        Figure f = event.getNode();
        switch (event.getEventType()) {
        case NODE_ADDED_TO_PARENT:
            handleFigureAdded(f);
            break;
        case NODE_REMOVED_FROM_PARENT:
            handleFigureRemoved(f);
            break;
        case NODE_ADDED_TO_TREE:
            handleFigureRemovedFromDrawing(f);
            break;
        case NODE_REMOVED_FROM_TREE:
            for (Figure d : f.preorderIterable()) {
                getSelectedFigures().remove(d);
            }
            repaint();
            break;
        case NODE_CHANGED:
            handleNodeChanged(f);
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
        default:
            throw new UnsupportedOperationException(event.getEventType()
                    + " not supported");
        }
    };
    private final InvalidationListener modelInvalidationListener = o -> repaint();

    private SimpleDrawingViewNode node;
    /**
     * Maps each JavaFX node to a figure in the drawing.
     */
    private final Map<Node, Figure> nodeToFigureMap = new IdentityHashMap<>();
    /**
     * Maps each JavaFX node to a handle in the drawing view.
     */
    private final Map<Node, Handle> nodeToHandleMap = new LinkedHashMap<>();
    private Pane overlaysPane;
    private Group overlaysSubScene;
    private Bounds previousScaledBounds = null;
    private boolean recreateHandles;
    boolean renderIntoImage = false;

    /**
     * The name of the margin property.
     */
    public final static String MARGIN_PROPERTY = "margin";

    /**
     * Margin around the drawing.
     */
    private final ObjectProperty<Insets> margin = new NonnullProperty<Insets>(this, MARGIN_PROPERTY, new Insets(20, 20, 20, 20));

    {
        margin.addListener(observable -> updateLayout());
    }

    private Runnable repainter = null;
    /**
     * This is the JavaFX Node which is used to represent this drawing view. in
     * a JavaFX scene graph.
     */
    private Pane rootPane;
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
    private Transform viewToWorldTransform = null;
    private Transform worldToViewTransform = null;
    /**
     * The zoom factor.
     */
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, ZOOM_FACTOR_PROPERTY, 1.0) {

        @Override
        protected void fireValueChangedEvent() {
            super.fireValueChangedEvent();
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
        }
    };

    {
        constrainer.addListener((o, oldValue, newValue) -> updateConstrainer(oldValue, newValue));
    }

    public SimpleDrawingView() {
        init();
    }

    @Override
    public ObjectProperty<Layer> activeLayerProperty() {
        return activeLayer;
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

    @Override
    public NonnullProperty<Constrainer> constrainerProperty() {
        return constrainer;
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
            return Shapes.outlineContains(Shapes.awtShapeFromFX(shape), new java.awt.geom.Point2D.Double(point.getX(), point.getY()),
                    shape.getStrokeWidth() * widthFactor + toleranceInLocal);
        } else if (node instanceof Rectangle) {
            return Geom.contains(node.getBoundsInLocal(), point, toleranceInLocal);
        } else if (node instanceof Group) {
            if (Geom.contains(node.getBoundsInLocal(), point, toleranceInLocal)) {
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
    protected void createHandles(Map<Figure, List<Handle>> handles) {
        ArrayList<Figure> selection = new ArrayList<>(getSelectedFigures());
        if (selection.size() > 1) {
            if (getAnchorHandleType() != null) {
                Figure anchor = selection.get(0);
                List<Handle> list = handles.computeIfAbsent(anchor, k -> new ArrayList<>());
                anchor.createHandles(getAnchorHandleType(), list);
            }
            if (getLeadHandleType() != null) {
                Figure anchor = selection.get(selection.size() - 1);
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

    @Override
    public void deleteSelection() {
        ArrayList<Figure> figures = new ArrayList<>(getSelectedFigures());
        DrawingModel model = getModel();
        for (Figure f : figures) {
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

    @Override
    public void duplicateSelection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Figure findFigure(double vx, double vy) {
        Drawing dr = getDrawing();
        Figure f = findFigureRecursive((Parent) getNode(dr), viewToWorld(vx, vy), TOLERANCE);
        return f;
    }

    /**
     * Finds a figure at the specified coordinate, but looks only at figures in
     * the specified set.
     * <p>
     * Uses a default tolerance value. See {@link #findFigure(double, double, java.util.Set, double)
     * }.
     *
     * @param vx point in view coordinates
     * @param vy point in view coordinates
     * @param figures figures of interest
     * @return a figure in the specified set which contains the point, or null.
     */
    @Override
    public Figure findFigure(double vx, double vy, Set<Figure> figures) {
        return findFigure(vx, vy, figures, TOLERANCE);
    }

    /**
     * Finds a figure at the specified coordinate, but looks only at figures in
     * the specified set.
     *
     * @param vx point in view coordinates
     * @param vy point in view coordinates
     * @param figures figures of interest
     * @param tolerance the number of pixels around the figure in view
     * coordinates, in which the the point is considered to be inside the figure
     * @return a figure in the specified set which contains the point, or null.
     */
    public Figure findFigure(double vx, double vy, Set<Figure> figures, double tolerance) {
        Node worldNode = getNode(getDrawing());
        Point2D pointInScene = worldNode.getLocalToSceneTransform().transform(viewToWorld(vx, vy));
        for (Figure f : figures) {
            Node n = getNode(f);
            Point2D pointInLocal = n.sceneToLocal(pointInScene);
            if (contains(n, pointInLocal, tolerance)) {
                return f;
            }
        }

        return null;
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
    public List<Figure> findFigures(double vx, double vy, boolean decompose) {
        Transform vt = getViewToWorld();
        Point2D pp = vt.transform(vx, vy);
        List<Figure> list = new ArrayList<>();
        findFiguresRecursive((Parent) figureToNodeMap.get(getDrawing()), pp, list, decompose);
        return list;
    }

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
        List<Figure> list = new ArrayList<>();
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

    private void findFiguresRecursive(Parent p, Point2D pp, List<Figure> found, boolean decompose) {
        double tolerance = TOLERANCE;
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            Figure f1 = nodeToFigureMap.get(n);
            if (f1 != null && f1.isSelectable()) {
                Point2D pl = n.parentToLocal(pp);
                if (contains(n, pl, tolerance)) { // only drill down if the parent contains the point
                    Figure f = nodeToFigureMap.get(n);
                    if (f != null && f.isSelectable() && f1.isVisible()) {
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
    public Handle findHandle(double vx, double vy) {
        if (recreateHandles) {
            return null;
        }
        for (Map.Entry<Node, Handle> e : new ReversedList<>(nodeToHandleMap.entrySet())) {
            final Node node = e.getKey();
            final Handle handle = e.getValue();
            if (!handle.isSelectable()) {
                continue;
            }
            if (handle.contains(this, vx, vy, TOLERANCE_SQUARED)) {
                return handle;
            } else {
                if (contains(node, new Point2D(vx, vy), TOLERANCE)) {
                    return handle;
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

    @Override
    public DrawingModel getModel() {
        return drawingModel.get();
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public Node getNode(Figure f) {
        if (f == null) {
            return null;
        }
        Node n = figureToNodeMap.get(f);
        if (n == null) {
            n = f.createNode(this);
            figureToNodeMap.put(f, n);
            nodeToFigureMap.put(n, f);
            dirtyFigureNodes.add(f);
        }
        return n;
    }

    private ScrollPane getScrollPane() {
        return scrollPane;
    }

    @Override
    public Transform getViewToWorld() {
        if (viewToWorldTransform == null) {
            // We try to avoid the Scale transform as it is slower than a Translate transform
            Transform tr = new Translate(-drawingPane.getTranslateX() + overlaysPane.getTranslateX(), -drawingPane.getTranslateY() + overlaysPane.getTranslateX());
            double zoom = zoomFactor.get();
            viewToWorldTransform = (zoom == 1.0) ? tr : Transforms.concat(new Scale(1.0 / zoom, 1.0 / zoom), tr);
        }
        return viewToWorldTransform;
    }

    @Override
    public Transform getWorldToView() {
        if (worldToViewTransform == null) {
            // We try to avoid the Scale transform as it is slower than a Translate transform
            Transform tr = new Translate(drawingPane.getTranslateX() - overlaysPane.getTranslateX(), drawingPane.getTranslateY() - overlaysPane.getTranslateX());
            double zoom = zoomFactor.get();
            worldToViewTransform = (zoom == 1.0) ? tr : Transforms.concat(tr, new Scale(zoom, zoom));
        }
        return worldToViewTransform;
    }

    private void handleConstrainerInvalidated(Observable o) {
        invalidateConstrainerNode();
        repaint();
    }

    private void handleDrawingChanged() {
        clearNodes();
        clearSelection();
        drawingPane.getChildren().clear();
        activeLayer.set(null);
        Drawing d = getModel().getDrawing();
        drawing.set(d);
        if (d != null) {
            drawingPane.getChildren().add(getNode(d));
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

    private void handleNewDrawingModel(DrawingModel oldValue, DrawingModel newValue) {
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
            handleDrawingChanged();
            updateLayout();
            repaint();
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

    private void handleSubtreeNodesChanged(Figure figures) {
        for (Figure f : figures.preorderIterable()) {
            dirtyFigureNodes.add(f);
            dirtyHandles.add(f);
        }
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
        rootPane.getChildren().addAll(drawingSubScene, overlaysSubScene);

        drawingPane = new Group();
        drawingPane.setCache(false);
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

        // We use a change listener instead of an invalidation listener here,
        // because we only want to update the layout, when the new value is
        // different from the old value!
        drawingPane.layoutBoundsProperty().addListener(observer -> updateLayout());

        drawingModel.get().setRoot(new SimpleDrawing());
        handleNewDrawingModel(null, drawingModel.get());

        // Set stylesheet
        rootPane.getStylesheets().add("org/jhotdraw8/draw/SimpleDrawingView.css");

        // set root
        node = new SimpleDrawingViewNode();
        node.setCenter(rootPane);

        // install/deiinstall listeners to scrollpane
        node.sceneProperty().addListener(this::updateScrollPaneListeners);
    }
    /**
     * The id of the tool pane for CSS styling.
     */
    public static final String TOOL_PANE_ID = "toolPane";
    /**
     * The id of the canvas pane for CSS styling.
     */
    public static final String CANVAS_PANE_ID = "canvasPane";

    private ScrollPane scrollPane;
    private final InvalidationListener visibleRectChangedHandler = this::handleVisibleRectChanged;

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

    private void handleVisibleRectChanged(Observable o) {
        invalidateConstrainerNode();
        repaint();
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

    private void invalidateWorldViewTransforms() {
        worldToViewTransform = viewToWorldTransform = null;
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
    public NonnullProperty<DrawingModel> modelProperty() {
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
    public void scrollRectToVisible(Bounds boundsInView) {
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
        sp.setVvalue(Geom.clamp(y / height, 0.0, 1.0));
        sp.setHvalue(Geom.clamp(x / width, 0.0, 1.0));
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
    public ReadOnlyBooleanProperty selectionEmptyProperty() {
        return selectedFiguresProperty().emptyProperty();
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
        double dw = d.get(Drawing.WIDTH);
        double dh = d.get(Drawing.HEIGHT);
        int imgw = Math.min(16000, Math.max(1, (int) (dw)));
        int imgh = Math.min(16000, Math.max(1, (int) (dh)));

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

    private void updateNodes() {
        if (!renderIntoImage) {
            // create copies of the lists to allow for concurrent modification
            Figure[] copyOfDirtyFigureNodes = dirtyFigureNodes.toArray(new Figure[dirtyFigureNodes.size()]);
            dirtyFigureNodes.clear();
            for (Figure f : copyOfDirtyFigureNodes) {
                f.updateNode(this, getNode(f));
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
            updateConstrainerNode();
        }
    }

    private void updateConstrainerNode() {
        constrainerNodeValid = true;
        Constrainer c = getConstrainer();
        if (c != null) {
            c.updateNode(this);
        }
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

    private void updateTreeStructure(Figure parent) {
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
        handleSubtreeNodesChanged(parent);
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

    public ObservableList<String> getStylesheets() {
        return rootPane.getStylesheets();
    }

    public Insets getMargin() {
        return margin.get();
    }

    public void setMargin(Insets value) {
        margin.set(value);
    }

    /**
     * The top, right, bottom, and left margin around the drawing.
     * The default value is: {@code new Insets(20, 20, 20, 20)}.
     */
    public ObjectProperty<Insets> marginProperty() {
        return margin;
    }

}
