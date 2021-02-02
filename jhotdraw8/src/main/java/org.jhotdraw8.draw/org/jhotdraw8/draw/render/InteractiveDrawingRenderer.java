/*
 * @(#)InteractiveDrawingRenderer.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.render;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Shape;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.AbstractPropertyBean;
import org.jhotdraw8.beans.NonNullObjectProperty;
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.model.SimpleDrawingModel;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.tree.TreeModelEvent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static org.jhotdraw8.draw.render.InteractiveHandleRenderer.contains;


public class InteractiveDrawingRenderer extends AbstractPropertyBean {
    public static final String RENDER_CONTEXT_PROPERTY = "renderContext";
    public static final String MODEL_PROPERTY = "model";
    public static final String DRAWING_VIEW_PROPERTY = "drawingView";
    private final @NonNull NonNullObjectProperty<RenderContext> renderContext //
            = new NonNullObjectProperty<>(this, RENDER_CONTEXT_PROPERTY, new SimpleRenderContext());
    private final @NonNull NonNullObjectProperty<DrawingModel> model //
            = new NonNullObjectProperty<>(this, MODEL_PROPERTY, new SimpleDrawingModel());

    private final @NonNull Group drawingPane = new Group();
    private final ObjectProperty<Bounds> clipBounds = new SimpleObjectProperty<>(this, "clipBounds",
            new BoundingBox(0, 0, 800, 600));
    /**
     * This is the set of figures which are out of sync with their JavaFX node.
     */
    private final Set<Figure> dirtyFigureNodes = new HashSet<>();
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, "zoomFactor", 1.0);
    private final Map<Figure, Node> figureToNodeMap = new HashMap<>();
    private final Map<Node, Figure> nodeToFigureMap = new HashMap<>();
    private final @NonNull ObjectProperty<DrawingView> drawingView = new SimpleObjectProperty<>(this, DRAWING_VIEW_PROPERTY);
    private final @NonNull ObjectProperty<DrawingEditor> editor = new SimpleObjectProperty<>(this, DrawingView.EDITOR_PROPERTY, null);
    private @Nullable Runnable repainter = null;
    private final @NonNull Listener<TreeModelEvent<Figure>> treeModelListener = this::onTreeModelEvent;

    public InteractiveDrawingRenderer() {
        drawingPane.setManaged(false);
        model.addListener(this::onDrawingModelChanged);
        clipBounds.addListener(this::onClipBoundsChanged);
    }

    public ObjectProperty<Bounds> clipBoundsProperty() {
        return clipBounds;
    }

    public @NonNull ObjectProperty<DrawingView> drawingViewProperty() {
        return drawingView;
    }

    public @NonNull ObjectProperty<DrawingEditor> editorProperty() {
        return editor;
    }

    public DrawingView getDrawingView() {
        return drawingView.get();
    }

    public void setDrawingView(DrawingView drawingView) {
        this.drawingView.set(drawingView);
    }

    public @Nullable Figure findFigure(double vx, double vy, Predicate<Figure> predicate) {
        Drawing dr = getDrawing();
        Figure f = findFigureRecursive((Parent) getNode(dr),
                getDrawingView().viewToWorld(vx, vy),
                getEditor().getTolerance(), predicate);
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
    public @Nullable Figure findFigure(double vx, double vy, @NonNull Set<Figure> figures) {
        return findFigure(vx, vy, figures, getEditor().getTolerance());
    }

    /**
     * Finds a figure at the specified coordinate, but looks only at figures in
     * the specified set.
     *
     * @param vx        point in view coordinates
     * @param vy        point in view coordinates
     * @param figures   figures of interest
     * @param radius the number of pixels around the figure in view
     *                  coordinates, in which the the point is considered to be inside the figure
     * @return a figure in the specified set which contains the point, or null.
     */
    public @Nullable Figure findFigure(double vx, double vy, @NonNull Set<Figure> figures, double radius) {
        Node worldNode = getNode(getDrawing());
        if (worldNode != null) {
            Point2D pointInScene = worldNode.getLocalToSceneTransform().transform(
                    getDrawingView().viewToWorld(vx, vy));
            Figure closestFigure=null;
            double closestDistance=Double.POSITIVE_INFINITY;
            for (Figure f : figures) {
                if (f.isShowing()) {
                    Node n = getNode(f);
                    if (n != null) {
                        Point2D pointInLocal = n.sceneToLocal(pointInScene);
                        Double distance = contains(n, pointInLocal, radius);
                        if (distance !=null&&distance<closestDistance) {
                            closestFigure=f;
                            closestDistance=distance;
                            if (distance==0.0)break;
                        }
                    }
                }
            }
            return closestFigure;
        }

        return null;
    }


    public @Nullable Node findFigureNode(@NonNull Figure figure, double vx, double vy) {
        Node n = figureToNodeMap.get(figure);
        if (n == null) {
            return null;
        }
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

    private @Nullable Figure findFigureRecursive(@Nullable Parent p, @NonNull Point2D pp, double tolerance, Predicate<Figure> predicate) {
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
            if (contains(n, pl, tolerance)!=null) {
                Figure f = nodeToFigureMap.get(n);
                if (f == null || !predicate.test(f)) {
                    if (n instanceof Parent) {
                        f = findFigureRecursive((Parent) n, pl, tolerance, predicate);
                    }
                }
                if (f != null && predicate.test(f)) {
                    return f;
                }
            }
        }
        return null;
    }

    public @NonNull List<Map.Entry<Figure,Double>> findFigures(double vx, double vy, boolean decompose,Predicate<Figure> predicate) {
        Transform vt = getDrawingView().getViewToWorld();
        Point2D pp = vt.transform(vx, vy);
        List<Map.Entry<Figure,Double>> list = new ArrayList<>();
        double tolerance = getEditor().getTolerance();
        findFiguresRecursive((Parent) figureToNodeMap.get(getDrawing()), pp, list, decompose,
                predicate, tolerance);
        return list;
    }

    public @NonNull List<Map.Entry<Figure,Double>> findFiguresInside(double vx, double vy, double vwidth, double vheight, boolean decompose, Predicate<Figure> predicate) {
        Transform vt = getDrawingView().getViewToWorld();
        Point2D pxy = vt.transform(vx, vy);
        Point2D pwh = vt.deltaTransform(vwidth, vheight);
        BoundingBox r = new BoundingBox(pxy.getX(), pxy.getY(), pwh.getX(), pwh.getY());
        List<Map.Entry<Figure,Double>> list = new ArrayList<>();
        findFiguresInsideRecursive((Parent) figureToNodeMap.get(getDrawing()), r, list, decompose, predicate);
        return list;
    }

    private boolean findFiguresInsideRecursive(@NonNull Parent p, @NonNull Bounds pp, @NonNull List<Map.Entry<Figure,Double>> found, boolean decompose, Predicate<Figure> predicate) {
        boolean foundAFigure=false;
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
                    boolean test =predicate.test(f1);
                    boolean foundDecomposedFigure=false;
                    if (!test || decompose && f1.isDecomposable()) {
                        if (n instanceof Parent) {
                            foundAFigure|= foundDecomposedFigure = findFiguresInsideRecursive((Parent) n, pl, found, decompose, predicate);
                        }
                    }
                    if (test&&!foundDecomposedFigure) {
                        // XXX 0.0 is not correct, because we only checked the bounds
                        found.add(new AbstractMap.SimpleImmutableEntry<>(f1,0.0));
                        foundAFigure=true;
                    }
                }
            } else {
                Bounds pl = n.parentToLocal(pp);
                if (n.intersects(pl)) { // only drill down if the parent intersects the point
                    if (n instanceof Parent) {
                        foundAFigure|=findFiguresInsideRecursive((Parent) n, pl, found, decompose, predicate);
                    }
                }
            }
        }
        return foundAFigure;
    }

    public @NonNull List<Figure> findFiguresIntersecting(double vx, double vy, double vwidth, double vheight, boolean decompose, Predicate<Figure> predicate) {
        Transform vt = getDrawingView().getViewToWorld();
        Point2D pxy = vt.transform(vx, vy);
        Point2D pwh = vt.deltaTransform(vwidth, vheight);
        BoundingBox r = new BoundingBox(pxy.getX(), pxy.getY(), pwh.getX(), pwh.getY());
        List<Figure> list = new ArrayList<>();
        findFiguresIntersectingRecursive((Parent) figureToNodeMap.get(getDrawing()), r, list, decompose, predicate);
        return list;
    }

    private boolean findFiguresIntersectingRecursive(@NonNull Parent p, @NonNull Bounds pp, @NonNull List<Figure> found, boolean decompose, Predicate<Figure> predicate) {
        boolean foundAFigure=false;
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            Bounds pl = n.parentToLocal(pp);
            if (n.intersects(pl)) { // only drill down if the parent intersects the point
                Figure f = nodeToFigureMap.get(n);
                boolean test = f!=null&&predicate.test(f);
                if (!test || decompose && f.isDecomposable()) {
                    if (n instanceof Parent) {
                        foundAFigure|=findFiguresIntersectingRecursive((Parent) n, pl, found, decompose, predicate);
                    }
                }
                if (test&&!foundAFigure) {
                    found.add(f);
                    foundAFigure=true;
                }
            }
        }
        return foundAFigure;
    }

    private boolean findFiguresRecursive(@NonNull Parent p, @NonNull Point2D pp, @NonNull List<Map.Entry<Figure,Double>> found, boolean decompose,
                                      Predicate<Figure> figurePredicate,  double tolerance) {
        boolean foundAFigure=false;
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            Figure f1 = nodeToFigureMap.get(n);
            if (f1 != null) {
                Point2D pl = n.parentToLocal(pp);
                Double distance=contains(n, pl, tolerance);
                if (distance!=null) { // only drill down if the parent contains the point
                    boolean test = figurePredicate.test(f1);
                    boolean addedDecomposedFigure=false;
                    if (!test||decompose&&f1.isDecomposable()) {
                        if (n instanceof Parent) {
                            addedDecomposedFigure= foundAFigure|=findFiguresRecursive((Parent) n, pl, found, decompose, figurePredicate,tolerance);
                        }
                    }
                    if (test&&!addedDecomposedFigure) {
                        found.add(new AbstractMap.SimpleImmutableEntry<>(f1,distance));
                        foundAFigure=true;
                    }
                }
            } else {
                Point2D pl = n.parentToLocal(pp);
                if (contains(n, pl, tolerance)!=null) { // only drill down if the parent intersects the point
                    if (n instanceof Parent) {
                        foundAFigure|=findFiguresRecursive((Parent) n, pl, found, decompose, figurePredicate,tolerance);
                    }
                }
            }
        }
        return foundAFigure;
    }

    public Bounds getClipBounds() {
        return clipBounds.get();
    }

    public void setClipBounds(Bounds clipBounds) {
        this.clipBounds.set(clipBounds);
    }

    public @Nullable Drawing getDrawing() {
        return getModel() == null ? null : getModel().getDrawing();
    }

    DrawingEditor getEditor() {
        return editorProperty().get();
    }

    public DrawingModel getModel() {
        return model.get();
    }

    public void setModel(DrawingModel model) {
        this.model.set(model);
    }

    public Node getNode() {
        return drawingPane;
    }

    public @Nullable Node getNode(@Nullable Figure f) {
        if (f == null) {
            return null;
        }
        Node n = figureToNodeMap.get(f);
        if (n == null) {
            n = f.createNode(getRenderContext());
            figureToNodeMap.put(f, n);
            nodeToFigureMap.put(n, f);
            dirtyFigureNodes.add(f);
            repaint();
        }
        return n;
    }

    public @NonNull NonNullObjectProperty<RenderContext> renderContextProperty() {
        return renderContext;
    }

    public @NonNull RenderContext getRenderContext() {
        return renderContext.get();
    }

    public void setRenderContext(@NonNull RenderContext newValue) {
        renderContext.set(newValue);
    }

    public double getZoomFactor() {
        return zoomFactorProperty().get();
    }

    public void setZoomFactor(double newValue) {
        zoomFactorProperty().set(newValue);
    }

    private boolean hasNode(Figure f) {
        return figureToNodeMap.containsKey(f);
    }

    private void invalidateFigureNode(Figure f) {
        dirtyFigureNodes.add(f);
    }

    private void invalidateLayerNodes() {
        Drawing drawing = getDrawing();
        if (drawing != null) {
            dirtyFigureNodes.addAll(drawing.getChildren());
        }
    }

    public @NonNull NonNullObjectProperty<DrawingModel> modelProperty() {
        return model;
    }

    private void onClipBoundsChanged(Observable observable) {
        invalidateLayerNodes();
        repaint();
    }

    private void onDrawingModelChanged(Observable o, @Nullable DrawingModel oldValue, @Nullable DrawingModel newValue) {
        if (oldValue != null) {
            oldValue.removeTreeModelListener(treeModelListener);
        }
        if (newValue != null) {
            newValue.addTreeModelListener(treeModelListener);
            onRootChanged(newValue.getDrawing());
        }
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
    }

    private void onFigureRemovedFromDrawing(@NonNull Figure figure) {

    }

    private void onLayoutChanged(Figure f) {
        invalidateFigureNode(f);
        repaint();
    }

    private void onNodeChanged(@NonNull Figure figure) {
        invalidateFigureNode(figure);
        repaint();
    }

    private void onNodeRemovedFromTree(@NonNull Figure f) {
    }

    private void onPropertyValueChanged(Figure f) {
        invalidateFigureNode(f);
        repaint();
    }

    private void onRootChanged(Figure f) {
        ObservableList<Node> children = drawingPane.getChildren();
        children.setAll(getNode(f));
        dirtyFigureNodes.clear();
        dirtyFigureNodes.add(f);
        repaint();
    }

    private void onStyleChanged(Figure f) {
        invalidateFigureNode(f);
        repaint();
    }

    private void onSubtreeNodesChanged(@NonNull Figure figure) {
        for (Figure f : figure.preorderIterable()) {
            dirtyFigureNodes.add(f);
        }
        repaint();
    }

    private void onTransformChanged(Figure f) {
        invalidateFigureNode(f);
        repaint();
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
            onNodeRemovedFromTree(f);
            break;
        case NODE_CHANGED:
            onNodeChanged(f);
            break;
        case ROOT_CHANGED:
            onRootChanged(f);
            break;
        case SUBTREE_NODES_CHANGED:
            onSubtreeNodesChanged(f);
            break;
        default:
            throw new UnsupportedOperationException(event.getEventType()
                    + " not supported");
        }
    }

    private void paint() {
        updateRenderContext();
        getModel().validate(getRenderContext());
        updateNodes();

    }

    private void updateRenderContext() {
        set(RenderContext.CLIP_BOUNDS, getClipBounds());
        DefaultUnitConverter units = new DefaultUnitConverter(90, 1.0, 1024.0 / getZoomFactor(), 768 / getZoomFactor());
        set(RenderContext.UNIT_CONVERTER_KEY, units);
    }

    private void removeNode(Figure f) {
        Node oldNode = figureToNodeMap.remove(f);
        if (oldNode != null) {
            nodeToFigureMap.remove(oldNode);
        }
        dirtyFigureNodes.remove(f);
    }

    public void repaint() {
        if (repainter == null) {
            repainter = () -> {
                repainter = null;
                paint();
            };
            Platform.runLater(repainter);
        }
    }

    private void updateNodes() {
        // FIXME we only want to update nodes inside visibleRectInWorld
        Bounds visibleRectInWorld = getClipBounds();

        // create copies of the lists to allow for concurrent modification
        Figure[] copyOfDirtyFigureNodes = dirtyFigureNodes.toArray(new Figure[0]);
        dirtyFigureNodes.clear();
        for (Figure f : copyOfDirtyFigureNodes) {
            Node node = getNode(f);
            if (!f.isShowing() || node == null) {
                if (node != null) {
                    node.setVisible(false);
                }
                // wont otherwise be updated and thus remains dirty!
                dirtyFigureNodes.add(f);
                continue;
            }
            f.updateNode(getRenderContext(), node);
        }
    }

    public @NonNull DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }
}
