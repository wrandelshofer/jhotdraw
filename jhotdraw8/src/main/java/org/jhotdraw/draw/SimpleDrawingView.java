/* @(#)SimpleDrawingView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.draw.handle.HandleType;
import org.jhotdraw.draw.model.DrawingModelEvent;
import org.jhotdraw.draw.model.DrawingModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw.beans.NonnullProperty;
import org.jhotdraw.draw.constrain.Constrainer;
import org.jhotdraw.draw.constrain.NullConstrainer;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.event.Listener;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.HandleEvent;
import static java.lang.Math.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.jhotdraw.app.EditableComponent;
import org.jhotdraw.beans.SimplePropertyBean;
import org.jhotdraw.draw.model.SimpleDrawingModel;
import org.jhotdraw.geom.Geom;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class SimpleDrawingView extends SimplePropertyBean implements DrawingView {

    private static class FixedSizedGroup extends Group {

    }

    private Group drawingSubScene;
    private Group overlaysSubScene;

    private BorderPane toolPane;

    private Group handlesPane;

    private Group drawingPane;
    private Pane overlaysPane;

    /**
     * The number of nodes that are maximally updated per frame.
     */
    private int maxUpdate = 100;

    /**
     * This is the JavaFX Node which is used to represent this drawing view. in
     * a JavaFX scene graph.
     */
    private StackPane stackPane;

    private class SimpleDrawingViewNode extends StackPane implements EditableComponent {

        public SimpleDrawingViewNode() {
            setFocusTraversable(true);
        }

        private ReadOnlyBooleanWrapper selectionEmpty = new ReadOnlyBooleanWrapper(this, EditableComponent.SELECTION_EMPTY);

        {
            selectionEmpty.bind(selectedFigures.emptyProperty());
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
            return selectionEmpty;
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
            switch (event.getEventType()) {
            case FIGURE_ADDED_TO_PARENT:
                handleFigureAdded(event.getFigure());
                break;
            case FIGURE_REMOVED_FROM_PARENT:
                handleFigureRemoved(event.getFigure());
                break;
            case FIGURE_ADDED_TO_DRAWING:
                // not my business
                break;
            case FIGURE_REMOVED_FROM_DRAWING:
                // not my business
                break;
            case NODE_INVALIDATED:
                handleNodeInvalidated(event.getFigure());
                break;
            case LAYOUT_INVALIDATED:
                // not my business
                break;
            case ROOT_CHANGED:
                updateDrawing();
                updateLayout();
                repaint();
                break;
            case SUBTREE_NODES_INVALIDATED:
                updateTreeNodes(event.getFigure());
                repaint();
                break;
            case SUBTREE_STRUCTURE_CHANGED:
                updateTreeStructure(event.getFigure());
                break;
            case CONNECTION_CHANGED:
            case TRANSFORM_CHANGED:
                // not my business
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
     * The constrainerProperty holds the constrainer for this drawing view
     */
    private final NonnullProperty<Constrainer> constrainer = new NonnullProperty<>(this, CONSTRAINER_PROPERTY, new NullConstrainer());
    private boolean handlesAreValid;

    /**
     * The selectedFiguresProperty holds the list of selected figures.
     */
    private final ReadOnlySetProperty<Figure> selectedFigures = new ReadOnlySetWrapper<>(this, SELECTED_FIGURES_PROPERTY, FXCollections.observableSet(new LinkedHashSet<Figure>())).getReadOnlyProperty();
    private Transform viewToDrawingTransform = null;
    private Transform drawingToViewTransform = null;

    private final double TOLERANCE = 10;

    /**
     * Installs a handler for changes in the selectionProperty.
     */
    {
        selectedFigures.addListener((Observable o) -> {
            invalidateHandles();
            repaint();
        });
    }

    private final ObjectProperty<Tool> tool = new SimpleObjectProperty<>(this, TOOL_PROPERTY);

    {
        tool.addListener((observable, oldValue, newValue) -> updateTool(oldValue, newValue));
    }
    private final ObjectProperty<Handle> activeHandle = new SimpleObjectProperty<>(this, ACTIVE_HANDLE_PROPERTY);
    private final ObjectProperty<HandleType> handleType = new SimpleObjectProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);

    {
        handleType.addListener((observable, oldValue, newValue) -> {
            invalidateHandles();
            repaint();
        });
    }
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
    private final HashMap<Node, Handle> nodeToHandleMap = new HashMap<>();
    /**
     * Maps each JavaFX node to a figure in the drawing.
     */
    private final HashMap<Node, Figure> nodeToFigureMap = new HashMap<>();
    /**
     * Maps JavaFX nodes to a figure. Note that the DrawingView may contain
     * JavaFX nodes which have no mapping. this is usually the case, when a
     * Figure is represented by multiple nodes. Then only the parent of these
     * nodes is associated with the figure.
     */
    private final HashMap<Figure, Node> figureToNodeMap = new HashMap<>();
    /**
     * This is the set of figures which are out of sync with their JavaFX node.
     */
    private final HashSet<Figure> dirtyFigureNodes = new HashSet<>();
    /**
     * This is the set of handles which are out of sync with their JavaFX node.
     */
    private final HashSet<Figure> dirtyHandles = new HashSet<>();
    /**
     * The set of all handles which were produced by selected figures.
     */
    private final HashMap<Figure, List<Handle>> handles = new HashMap<>();
    /**
     * The set of all secondary handles. One handle at a time may create
     * secondary handles.
     */
    private final LinkedList<Handle> secondaryHandles = new LinkedList<>();

    private Runnable repainter = null;

    private void invalidateFigureNode(Figure f) {
        dirtyFigureNodes.add(f);
        if (handles.containsKey(f)) {
            dirtyHandles.add(f);
        }
    }

    @Override
    public Transform getDrawingToView() {
        if (drawingToViewTransform == null) {
            Transform st = new Scale(zoomFactor.get(), zoomFactor.get());
            Transform tr = new Translate(drawingPane.getTranslateX(), drawingPane.getTranslateY());
            drawingToViewTransform = tr.createConcatenation(st);
        }
        return drawingToViewTransform;
    }

    @Override
    public Transform getViewToDrawing() {
        if (viewToDrawingTransform == null) {
            Transform st = new Scale(1.0 / zoomFactor.get(), 1.0 / zoomFactor.get());
            Transform tr = new Translate(-drawingPane.getTranslateX(), -drawingPane.getTranslateY());
            viewToDrawingTransform = st.createConcatenation(tr);
        }
        return viewToDrawingTransform;
    }

    /**
     * Updates the layout of the drawing pane and the panes laid over it.
     */
    private void updateLayout() {
        Bounds bounds = drawingPane.getLayoutBounds();

        double f = getZoomFactor();
        double x = bounds.getMinX() * f;
        double y = bounds.getMinY() * f;
        double w = bounds.getWidth() * f;
        double h = bounds.getHeight() * f;

        drawingPane.setTranslateX(max(0, -x));
        drawingPane.setTranslateY(max(0, -y));

        toolPane.resize(w, h);
        toolPane.layout();

        stackPane.setPrefSize(w, h);

        invalidateDrawingViewTransforms();
        invalidateHandleNodes();
    }

    @Override
    public ReadOnlyObjectProperty<Drawing> drawingProperty() {
        return drawing.getReadOnlyProperty();
    }

    public SimpleDrawingView() {
        init();
    }

    private void init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            stackPane = loader.load(SimpleDrawingView.class.getResourceAsStream("SimpleDrawingView.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        stackPane.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent evt) -> {
            if (!stackPane.isFocused()) {
                stackPane.requestFocus();
                if (!stackPane.getScene().getWindow().isFocused()) {
                    evt.consume();
                }
            }
        });
        stackPane.setFocusTraversable(true);
        focused.bind(stackPane.focusedProperty());

        drawingSubScene = new Group();
        drawingSubScene.setManaged(false);
        overlaysSubScene = new Group();
        overlaysSubScene.setManaged(false);
        stackPane.getChildren().addAll(drawingSubScene, overlaysSubScene);

        drawingPane = new Group();
        drawingPane.setScaleX(zoomFactor.get());
        drawingPane.setScaleY(zoomFactor.get());
        drawingSubScene.getChildren().add(drawingPane);

        toolPane = new BorderPane();
        toolPane.setBackground(Background.EMPTY);
        //toolPane.setBackground(new Background(new BackgroundFill(new Color(0,1,0,0.25),null,null)));
        toolPane.setManaged(false);
        handlesPane = new Group();
        handlesPane.setManaged(false);
        overlaysPane = new Pane();
        overlaysPane.setBackground(Background.EMPTY);
        overlaysPane.getChildren().addAll(handlesPane, toolPane);
        overlaysPane.setManaged(false);
        overlaysSubScene.getChildren().add(overlaysPane);

        drawingPane.layoutBoundsProperty().addListener(observer -> {
            updateLayout();

        });

        drawingModel.get().setRoot(new SimpleDrawing());
        handleNewDrawingModel(null, drawingModel.get());

        // Set stylesheet
        overlaysPane.getStylesheets().add("org/jhotdraw/draw/SimpleDrawingView.css");

        // set root
        node = new SimpleDrawingViewNode();
        node.getChildren().add(stackPane);
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

    private Property<Rectangle2D> boundsProperty;

    private void updateDrawing() {
        clearNodes();
        drawingPane.getChildren().clear();
        if (boundsProperty != null) {
            boundsProperty.unbind();
            boundsProperty = null;
            activeLayer.set(null);
        }
        Drawing d = getModel().getRoot();
        drawing.set(d);
        if (d != null) {
            boundsProperty = Drawing.BOUNDS.propertyAt(d.propertiesProperty());
            drawingPane.getChildren().add(getNode(d));
            dirtyFigureNodes.add(d);
            updateLayout();
            updateTreeNodes(d);
            repaint();
        }
    }

    private void updateTreeNodes(Figure parent) {
        dirtyFigureNodes.add(parent);
        dirtyHandles.add(parent);
        for (Figure child : parent.getChildren()) {
            updateTreeNodes(child);
        }
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
        updateTreeNodes(parent);
    }

    private void handleNewDrawingModel(DrawingModel oldValue, DrawingModel newValue) {
        if (oldValue != null) {
            oldValue.removeDrawingModelListener(modelHandler);
            drawing.setValue(null);
        }
        if (newValue != null) {
            newValue.addDrawingModelListener(modelHandler);
            updateDrawing();
            updateLayout();
        }
    }

    private void handleFigureAdded(Figure f) {
        handleFigureAdded0(f);
        repaint();
    }

    private void handleFigureAdded0(Figure f) {
        invalidateFigureNode(f);
        for (Figure child : f.childrenProperty()) {
            handleFigureAdded0(child);
        }
    }

    private void handleFigureRemoved(Figure f) {
        handleFigureRemoved0(f);
        repaint();
    }

    private void handleFigureRemoved0(Figure f) {
        for (Figure child : f.childrenProperty()) {
            handleFigureRemoved0(child);
        }
        removeNode(f);
        selectedFigures.remove(f);
        invalidateHandles();
    }

    private void handleNodeInvalidated(Figure f) {
        invalidateFigureNode(f);
        if (f == getDrawing()) {
            updateLayout();
        }
        repaint();
    }

    private void updateView() {
        getModel().validate();

        // create copies of the lists to allow for concurrent modification
        ArrayList<Figure> copyOfDirtyFigureNodes = new ArrayList<>(dirtyFigureNodes);
        ArrayList<Figure> copyOfDirtyHandles = new ArrayList<>(dirtyHandles);
        dirtyFigureNodes.clear();
        dirtyHandles.clear();

        for (Figure f : copyOfDirtyFigureNodes) {
            f.updateNode(this, getNode(f));
        }
        for (Figure f : copyOfDirtyHandles) {
            List<Handle> hh = handles.get(f);
            if (hh != null) {
                for (Handle h : hh) {
                    h.updateNode(this);
                }
            }
        }
        for (Handle h : secondaryHandles) {
            h.updateNode(this);
        }
    }

    /**
     * Repaints the view.
     */
    public void repaint() {
        if (repainter == null) {
            repainter = () -> {
                repainter = null;
                updateView();
                validateHandles();
            };
            Platform.runLater(repainter);
        }
    }

    @Override
    public ReadOnlyBooleanProperty focusedProperty() {
        return focused.getReadOnlyProperty();
    }

    @Override
    public ObjectProperty<Tool> toolProperty() {
        return tool;
    }

    @Override
    public ObjectProperty<Handle> activeHandleProperty() {
        return activeHandle;
    }

    @Override
    public ObjectProperty<HandleType> handleTypeProperty() {
        return handleType;
    }

    private void updateTool(Tool oldValue, Tool newValue) {
        if (oldValue != null) {
            Tool t = oldValue;
            toolPane.setCenter(null);
            t.setDrawingView(null);
        }
        if (newValue != null) {
            Tool t = newValue;
            toolPane.setCenter(t.getNode());
            t.setDrawingView(this);
        }
    }

    @Override
    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    public Handle findHandle(double vx, double vy) {
        for (Node n : handlesPane.getChildren()) {
            Point2D pl = n.parentToLocal(vx, vy);
            if (contains(n, pl, TOLERANCE)) {
                Handle h = nodeToHandleMap.get(n);
                if (h.isSelectable()) {
                    return h;
                }
            }
        }
        return null;
    }

    @Override
    public Figure findFigure(double vx, double vy) {
        Drawing dr = getDrawing();
        Figure f = findFigureRecursive((Parent) getNode(dr), viewToDrawing(vx, vy), 0.0);
        if (f == null) {
            f = findFigureRecursive((Parent) getNode(dr), viewToDrawing(vx, vy), TOLERANCE);
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
                if (f == null || !f.isSelectable() && !f.isDisabled()) {
                    if (n instanceof Parent) {
                        f = findFigureRecursive((Parent) n, pl, tolerance);
                    }
                }
                if (f != null && !f.isDisabled()) {
                    return f;
                }
            }
        }
        return null;
    }

    @Override
    public Figure findFigure(double vx, double vy, Set<Figure> figures) {
        Drawing dr = getDrawing();
        Figure f = findFigureRecursiveInSet((Parent) getNode(dr), viewToDrawing(vx, vy), figures);

        return f;
    }

    private Figure findFigureRecursiveInSet(Parent p, Point2D pp, Set<Figure> figures) {
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            if (!n.isVisible()) {
                continue;
            }
            Point2D pl = n.parentToLocal(pp);
            if (contains(n, pl, TOLERANCE)) {
                Figure f = nodeToFigureMap.get(n);
                if (f == null || !f.isSelectable() && !f.isDisabled()) {
                    if (n instanceof Parent) {
                        f = findFigureRecursiveInSet((Parent) n, pl, figures);
                    }
                }
                if (f != null && !f.isDisabled() && figures.contains(f)) {
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
        if (node instanceof Line) {
            Line line = (Line) node;
            boolean contains = Geom.lineContainsPoint(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), point.getX(), point.getY(), tolerance);
            return contains;
        } else if (node instanceof Group) {
            Bounds bounds = node.getBoundsInLocal();
            bounds = Geom.grow(bounds, tolerance, tolerance);
            if (bounds.contains(point)) {
                for (Node child : ((Group) node).getChildren()) {
                    if (contains(child, child.parentToLocal(point), tolerance)) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return node.contains(point);
        }
    }

    @Override
    public List<Figure> findFigures(double vx, double vy, boolean decompose) {
        Transform vt = getViewToDrawing();
        Point2D pp = vt.transform(vx, vy);
        List<Figure> list = new LinkedList<Figure>();
        findFiguresRecursive((Parent) figureToNodeMap.get(getDrawing()), pp, list, decompose);
        return list;
    }

    private void findFiguresRecursive(Parent p, Point2D pp, List<Figure> found, boolean decompose) {
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            Figure f1 = nodeToFigureMap.get(n);
            if (f1 != null && f1.isSelectable()) {
                Point2D pl = n.parentToLocal(pp);
                if (contains(n, pl, TOLERANCE)) { // only drill down if the parent contains the point
                    Figure f = nodeToFigureMap.get(n);
                    if (f != null && f.isSelectable() && !f.isDisabled()) {
                        found.add(f);
                    }
                    if (f == null || !f.isSelectable() || decompose && f.isDecomposable() && !f.isDisabled()) {
                        if (n instanceof Parent) {
                            findFiguresRecursive((Parent) n, pl, found, decompose);
                        }
                    }
                }
            } else {
                Point2D pl = n.parentToLocal(pp);
                if (contains(n, pl, TOLERANCE)) { // only drill down if the parent intersects the point
                    if (n instanceof Parent) {
                        findFiguresRecursive((Parent) n, pl, found, decompose);
                    }
                }
            }
        }
    }

    @Override
    public List<Figure> findFiguresInside(double vx, double vy, double vwidth, double vheight, boolean decompose) {
        Transform vt = getViewToDrawing();
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
            Figure f1 = nodeToFigureMap.get(n);
            if (f1 != null && f1.isSelectable()) {
                Bounds pl = n.parentToLocal(pp);
                if (pl.contains(n.getBoundsInLocal())) { // only drill down if the parent bounds contains the point
                    Figure f = nodeToFigureMap.get(n);
                    if (f != null && f.isSelectable() && !f.isDisabled()) {
                        found.add(f);
                    }
                    if (f == null || !f.isSelectable() || decompose && f.isDecomposable() && !f.isDisabled()) {
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
        Transform vt = getViewToDrawing();
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
                if (f != null && f.isSelectable() && !f.isDisabled()) {
                    found.add(f);
                }
                if (f == null || !f.isSelectable() || decompose && f.isDecomposable() && !f.isDisabled()) {
                    if (n instanceof Parent) {
                        findFiguresIntersectingRecursive((Parent) n, pl, found, decompose);
                    }
                }
            }
        }
    }

    @Override
    public ReadOnlySetProperty<Figure> selectedFiguresProperty() {
        return selectedFigures;
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
        HashSet<Figure> result = new HashSet<>();
        for (Map.Entry<Figure, List<Handle>> entry : handles.entrySet()) {
            if (figures.contains(entry.getKey())) {
                for (Handle h : entry.getValue()) {
                    if (h.isCompatible(master)) {
                        result.add(entry.getKey());
                        break;
                    }
                }
            }
        }
        return result;
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

    private void invalidateHandleNodes() {
        dirtyHandles.addAll(handles.keySet());
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
        for (Map.Entry<Figure, List<Handle>> entry : handles.entrySet()) {
            dirtyHandles.add(entry.getKey());
            for (Handle handle : entry.getValue()) {
                Node n = handle.getNode();
                nodeToHandleMap.put(n, handle);
                handlesPane.getChildren().add(n);
                handle.updateNode(this);
            }
        }
    }

    /**
     * Creates selection handles and adds them to the provided list.
     *
     * @param handles The provided list
     */
    protected void createHandles(HashMap<Figure, List<Handle>> handles) {
        HandleType handleType = getHandleType();
        for (Figure figure : getSelectedFigures()) {
            List<Handle> list = handles.get(figure);
            if (list == null) {
                list = new ArrayList<>();
                handles.put(figure, list);
            }
            figure.createHandles(handleType, this, list);
            handles.put(figure, list);
        }
    }

    private void invalidateDrawingViewTransforms() {
        drawingToViewTransform = viewToDrawingTransform = null;
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
    private void selectAll() {
        ArrayList<Figure> figures = new ArrayList<>();
        Drawing d = getDrawing();
        if (d != null) {
            for (Figure layer : d.getChildren()) {
                if (!layer.isDisabled()) {
                    for (Figure f : layer.getChildren()) {
                        if (!f.isDisabled() && f.isSelectable()) {
                            figures.add(f);
                        }
                    }
                }
            }
        }
        getSelectedFigures().clear();
        getSelectedFigures().addAll(figures);
    }

    private void clearSelection() {
        getSelectedFigures().clear();
    }

    private void deleteSelection() {
        ArrayList<Figure> figures = new ArrayList<>(getSelectedFigures());
        DrawingModel model = getModel();
        for (Figure f : figures) {
            if (f.isDeletable()) {
                model.disconnect(f);
                model.removeFromParent(f);
            }
        }
    }

    private void duplicateSelection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void cut() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void paste() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
